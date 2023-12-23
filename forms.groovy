package projects.helpdesk.behaviours

def selectedIssueType = issueContext.issueType.name
def fullname = getFieldByName("Fullname")
def email = getFieldByName("Email")
def username = getFieldByName("Username")

def fields = [fullname, email, username]
def type = getFieldById("customfield_11400")


if(selectedIssueType == "Access") {
    fields.each { field ->
        field.setRequired(true)
    }
    
}
else if(selectedIssueType == "Help") {
    getFieldById("description").setRequired(true)
}

def map = [
    "Access":["New account", "Remove access", "Grant access"],
    "Project":["New project", "Project modification"],
    "Help":["Question", "Bug"]
]

type.setFieldOptions(map[selectedIssueType])
