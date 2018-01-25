# Gerrit/Jira Commit Hook in Java

This is a Git commit hook in Java that will extend the commit message provided by the user in two ways:

* add the headline (e.g. "summary") of the Jira tickets referenced
* add a Change-Id for Gerrit

It can be easily extended or adapted.

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

Jira ticket IDs can be provided one per line at the beginning of the commit message. The hook will retrieve the headlines from Jira with the REST API and add them to the commit message.

Additionaly, the hook will add a "Jira-Id" footer line, so that is easy for other programs or script to find where issues have been fixed. For instance, Gerrit can use this information as "trackingid", so that searching for "bug:ABC-123" in the web interface will lead to the relevant commit.

## Single ticket

Initial commit message:

```
ABC-123
```

Output:

```
ABC-123 <headline of the ticket ABC-123 from Jira"

Jira-Id: ABC-123
```

Additional text lines are accepted and will be preserved.

## Multiple tickets

Initial commit message:

```
ABC-123
PROJECT-456

We still need to fix ABC-999
```

Output:

```
Issues ABC-123 PROJECT-456

ABC-123     <headline of the ticket ABC-123 from Jira"
PROJECT-456 <headline of the ticket PROJECT-456 from Jira"

We still need to fix ABC-999

Jira-Id: ABC-123
Jira-Id: PROJECT-456
```

Additional text lines are accepted and will be preserved.

# Usage

The source code must be adapted to you own needs, the most obvious reason being that the parameters for Jira are hard-coded. The design of the application is defined in a way to make adaptations and extensions easy.

To build the commit hook, use the following Gradle task:

```
gradlew createShellScript
```

It will generate ```build/commit-msg``` shell script, which has two functions: extract the embedded JAR file on the first execution, and start the JAR on every execution. Put this script in your Git repository in the subdirectory ```.git/hooks```:

```
cp build/commit-msg <repopath>/.git/hooks/
```
