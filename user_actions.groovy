package console

import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.*
import com.atlassian.sal.api.user.UserManager
import com.atlassian.jira.bc.user.search.UserSearchService
import com.atlassian.jira.bc.user.search.UserSearchParams
import com.atlassian.jira.application.ApplicationAuthorizationService
import com.atlassian.jira.application.ApplicationKeys
import com.atlassian.jira.bc.security.login.LoginInfo

//===PARAMS===
//set the action to take
//setting the "activate" param will make the script operate on inactive users only
//setting the "deactivate" param will make the script operate on active users only
//accepted values: activate/deactivate
String action_to_take = "deactivate" 
//set the amount of days against which the script will check the last login time 
//you can calculate the days on https://www.timeanddate.com/date/duration.html
//accepted values: numbers
int threshold = 16
//tells the scripts if it should take users from the time AFTER of BEFORE the threshold
//default: before 
//accepted values: after/before
String interval_direction = "before"
//deactivate users with specific license type
//accepted values: service_desk/software
//if no value provided, both will be considered
String license_to_find = ""
//specify the users domain
//Set as empty String if you want to include all domains
String user_domain = "" 
//run the script in dry_run mode first. It won`t take any action but will provide you the list of users to operate on
//default = true
//accepted values: true/false
boolean dry_run = true
//here you can specify whitelists for users which shouldn`t be deactivated or activated
List not_deactivate = []
List not_activate = []
//===========


def actOnUser(ApplicationUser user, String action) {
    boolean isActive
    String takenAction
    if(action == "activate") {
        isActive = true
        takenAction = "activated" 
    }
    else if(action == "deactivate") {
        isActive = false
        takenAction = "deactivated" 
    }
    def userService = ComponentAccessor.getComponent(UserService)
    def updateUser = userService.newUserBuilder(user).active(isActive).build()
    def updateUserValidationResult = userService.validateUpdateUser(updateUser)

    if (!updateUserValidationResult.valid) {
        log.error "Update of ${user.name} failed. ${updateUserValidationResult.errorCollection}"
        return
    }

    userService.updateUser(updateUserValidationResult)
    log.info "${updateUser.name} " + takenAction
}

def userCheck(ApplicationUser user, List whitelist, LoginInfo loginfo) {

    //user must logged in at least once and cannot be on the whitelist
    if (loginfo.getLastLoginTime() != null && !whitelist.contains(user.getEmailAddress())) {
        return true 
    }
    else {
        return false
    }
}

def searchUsers(String action_to_take, boolean dry_run, String user_domain, int threshold, String interval_direction, List not_activate, List not_deactivate, String license_to_find) {
    def applicationAuthorizationService = ComponentAccessor.getComponent(ApplicationAuthorizationService)
    def userManager = ComponentAccessor.getUserManager()
    def loginManager = ComponentAccessor.getComponent(LoginManager)
    def userSearchService2 = ComponentAccessor.getComponent(UserSearchService.class);

    ArrayList users_to_take_action_on = new ArrayList()
    String user_info
    ArrayList users_info = new ArrayList()
    Date today = new Date()
    boolean if_include_active
    boolean if_include_inactive
    if(action_to_take == "activate") {
        if_include_active = false
        if_include_inactive = true 
    }
    else if(action_to_take == "deactivate") {
        if_include_active = true
        if_include_inactive = false 
    }

    UserSearchParams userSearchParams2 = (new UserSearchParams.Builder()).allowEmptyQuery(true).includeActive(if_include_active).includeInactive(if_include_inactive).canMatchEmail(true).maxResults(100000).build();

    userSearchService2.findUsers(user_domain, userSearchParams2).each{ user->
        LoginInfo loginfo = loginManager.getLoginInfo(user.getUsername())

        if(action_to_take == "activate") {
            if(userCheck(user, not_activate, loginfo) == false) {
                return
            }

        }
        else if(action_to_take == "deactivate") {
            if(userCheck(user, not_deactivate, loginfo) == false) {
                return
            }
        }

        def lastLogOn = new Date(loginfo.getLastLoginTime())
        def difference = today.minus(lastLogOn)
        String license_type = ""

        //<= - for users that logged in AFTER threshold; >= - BEFORE the threshold
        if(interval_direction == "before") {
            if(!(difference >= threshold)) {
                return
            }
        }
        else if(interval_direction == "after") {
            if(!(difference <= threshold)) {
                return
            }
        }

        //checking the licnese type
        if(applicationAuthorizationService.canUseApplication(user, ApplicationKeys.SERVICE_DESK) and (license_to_find == "service_desk" || license_to_find =="")) {
            license_type += " SERVICE_DESK"
        }
        if(applicationAuthorizationService.canUseApplication(user, ApplicationKeys.SOFTWARE)and (license_to_find == "software" || license_to_find =="")) {
            license_type += " SOFTWARE"
        }

        if(!dry_run) {
            if(license_type !="") {
                actOnUser(user, action_to_take)
            }
        }

        if(dry_run) {
            if(license_type !="") {
                users_to_take_action_on.add(user.getEmailAddress())

                user_info = user.getDisplayName() + ";" + user.getEmailAddress() + ";" + lastLogOn + ";" + license_type + "\n"
                users_info.add(user_info)
            }
        }

    }        

    if(dry_run) {
        log.error "Action to be taken: " +  action_to_take + "\n\n Users details: " + users_info
    }

    return users_to_take_action_on

}


def users_to_take_action_on = searchUsers(action_to_take, dry_run, user_domain, threshold, interval_direction, not_activate, not_deactivate, license_to_find)

if(dry_run) {
    return "Action to be taken: " +action_to_take+". It will affect "+users_to_take_action_on.size()+"users. If the array below looks OK, set dry_run = false and FIRE! You can see more details in the Logs tab. Users to be affected: " + users_to_take_action_on + "\n\n "
}
else {
    return "Taken action: " + action_to_take + ". Users affected: " + users_to_take_action_on

}
