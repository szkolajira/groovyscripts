package projects.helpdesk.behaviours

import com.atlassian.jira.component.ComponentAccessor

def type = getFieldById("customfield_11400")


def selectedType = type.getValue()

if(selectedType) {
    type.setHelpText("<a href='https://szkolajira.pl'>Documentation</a>")
}
else {
    type.setHelpText("Please choose a service type. <a href='https://szkolajira.pl'>Documentation</a>")
}