# Git Commit Hook

This is a Git commit hook that does two thinks:

* generate a Change-Id for Gerrit
* adds the summary of Jira tickets referenced

# Gerrit Change-Id

The Change-Id footer line is required for Gerrit. It is generated randomly. Example:

Initial commit message:

```
Add feature XYZ

This adds the feature XYZ according to the specification ABC. 

Open issues:
- crash on startup on Windows 7 
```

Output:

```
Add feature XYZ

This adds the feature XYZ according to the specification ABC. 

Open issues:
- crash on startup on Windows 7 

Change-Id: I1234567890123456789012345678901234567890
```

# Jira tickets

Jira ticket IDs can be provided one per line at the beginning of the commit message. The hook will retrieve the headlines from Jira with the REST API and update the commit message.

Additionaly, the hook will add a "Jira-Id" footer line, so that is easy for other programs or script to find where issues have been fixed. For instance, Gerrit can use this information as "trackingid", so that searching for "bug:ABC-123" in the web interface will lead to the relevant commit.

## Single ticket

Initial commit message:

```
ABC-123
```

Output:

```
ABC-123 Headline

Jira-Id: ABC-123
```

Additional comment lines are accepted and will be preserved.

## Multiple tickets

Initial commit message:

```
ABC-123
PROJECT-456
```

Output:

```
Issues ABC-123 PROJECT-456

ABC-123     Headline of ABC-123
PROJECT-456 Headline of PROJECT-456

Jira-Id: ABC-123
Jira-Id: PROJECT-456
```

Additional comment lines are accepted and will be preserved.
