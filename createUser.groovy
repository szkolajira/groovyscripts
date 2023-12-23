package projects.helpdesk.postfunctions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.user.UserService

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def userService = ComponentAccessor.getComponent(UserService)

def email = customFieldManager.getCustomFieldObject("customfield_11401")
def emailValue = issue.getCustomFieldValue(email)

def fullname = customFieldManager.getCustomFieldObject("customfield_11402")
def fullnameValue = issue.getCustomFieldValue(fullname)

def username = customFieldManager.getCustomFieldObject("customfield_11403")
def usernameValue = issue.getCustomFieldValue(username)

def pass = "pass123!"

def AdminUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

UserService.CreateUserRequest createUserRequest = UserService.CreateUserRequest
.withUserDetails(AdminUser, usernameValue, pass, emailValue, fullnameValue)
.performPermissionCheck(true)
.sendNotification(false)

UserService.CreateUserValidationResult result = userService.validateCreateUser(createUserRequest)

if(result.isValid()) {
    userService.createUser(result)
}


