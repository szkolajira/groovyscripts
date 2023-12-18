import com.atlassian.jira.component.ComponentAccessor
def issueManager = ComponentAccessor.getIssueManager()
def issueService = ComponentAccessor.getIssueService()

def issue = issueManager.getIssueByCurrentKey("TEST1-3")
def priority = issue.getPriority().name
def project = issue.getProjectObject()
def projectLead = project.getProjectLead()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

if(priority in ["Highest", "High"]) {
    def assignResult = issueService.validateAssign(currentUser, issue.id, projectLead.getUsername())
    issueService.assign(projectLead, assignResult)  
}
