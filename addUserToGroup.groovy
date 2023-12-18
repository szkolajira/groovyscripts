import com.atlassian.jira.component.ComponentAccessor

def userManager = ComponentAccessor.getUserManager()
def groupManager = ComponentAccessor.getGroupManager()
//opcjonalnie
// import com.atlassian.jira.security.groups.GroupManager
// def groupManager = ComponentAccessor.getComponent(GroupManager)

def username = "wojtek"
def userObject = userManager.getUserByName(username)

def groupname = "test1"
def targetGroup = groupManager.getGroup(groupname)

groupManager.addUserToGroup(userObject, targetGroup)
