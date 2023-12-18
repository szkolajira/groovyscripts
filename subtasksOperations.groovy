import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption

def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueByCurrentKey("APT-7")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def subtasks = issue.getSubTaskObjects()

if (subtasks){
    subtasks.each {subtask ->
        def currentDescription = subtask.getDescription()
        subtask.setDescription(currentDescription + " jestem subtaskiem!!")
        issueManager.updateIssue(currentUser, subtask, EventDispatchOption.ISSUE_UPDATED, false)        
    }
}
else {
    log.error("I do not have subtasks")
}
