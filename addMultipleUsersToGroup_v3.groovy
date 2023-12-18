import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager

def groupManager = ComponentAccessor.getGroupManager()
def issueManager = ComponentAccessor.getIssueManager()
def customFieldManager = ComponentAccessor.customFieldManager

def issue = issueManager.getIssueByCurrentKey("APT-7")

//get group from customfield
def group = customFieldManager.getCustomFieldObject('customfield_11202')
def groupValue = issue.getCustomFieldValue(group)[0]

//get users from customfield
def users = customFieldManager.getCustomFieldObject('customfield_11203')
def usersValue = issue.getCustomFieldValue(users)


//add each user to the group
usersValue.each { user -> 
    //check if user is active
    if(!user.isActive()) {
        log.error "user inactive: " + user
    }
    else if(groupManager.isUserInGroup(user, groupValue)) {
        log.error "user already in group: " + user
    }
    else {
        try {
            groupManager.addUserToGroup(user, groupValue)
	    }
        catch(Error) {
            log.error("there is an error: " + Error)
        }
    }
}
