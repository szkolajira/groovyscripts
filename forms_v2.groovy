package projects.helpdesk.behaviours

def selectedIssueType = issueContext.issueType.name
def fullname = getFieldByName("Fullname")
def email = getFieldByName("Email")
def username = getFieldByName("Username")

def fields = [fullname, email, username]
def type = getFieldById("customfield_11400")

def value = """h3. Please provide more info about your need 
* some data 
* some data"""

def value2 = """h3. Please provide more info about your need 
* some data 2 
* some data 2 """

def desc = getFieldById("description")

if(selectedIssueType == "Access") {
    fields.each { field ->
        field.setRequired(true)
    }
    desc.setFormValue(value)
    
}
else if(selectedIssueType == "Help") {
    desc.setRequired(true)

      desc.setFormValue(value2)
}

def map = [
    "Access":["New account", "Remove access", "Grant access"],
    "Project":["New project", "Project modification"],
    "Help":["Question", "Bug"]
]

type.setFieldOptions(map[selectedIssueType])
