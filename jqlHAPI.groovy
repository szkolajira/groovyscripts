Issues.search('project = APT and description is not empty').each { issue ->
    issue.update {
        setDescription {
            // add to the existing value
            append('SUFFIX ')
    
            // prepend to the existing value
            prepend('PREFIX ')
    
            // replace all instances of the first string with the second
            replace('first', 'second')
        }
    }
}
