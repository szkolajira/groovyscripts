import com.atlassian.jira.component.ComponentAccessor

def groupManager = ComponentAccessor.getGroupManager()

def issue = Issues.getByKey('APT-7')
def users = issue.getCustomFieldValue(11203)
def group = issue.getCustomFieldValue(11202)[0]

users.each { user ->
    //check if user is active
    if(!user.isActive()) {
        log.error "user inactive: " + user
    }
    else if(user in group.getMembers()) {
        log.error "user already in group: " + user
    }
    else {
        try {
	    group.add(user)
	}
        catch(Error) {
            log.error("there is an error: " + Error)
        }
    }
}
