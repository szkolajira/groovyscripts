import com.atlassian.jira.component.ComponentAccessor

def userManager = ComponentAccessor.getUserManager()
def groupManager = ComponentAccessor.getGroupManager()

def groupname = "test1"
def targetGroup = groupManager.getGroup(groupname)

ArrayList usersToAdd = new ArrayList()
usersToAdd.addAll("marek", "anna")

def userObject

usersToAdd.each { user -> 
    userObject = userManager.getUserByName(user)
    groupManager.addUserToGroup(userObject, targetGroup)    
}
