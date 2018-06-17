# Git commit hook with Gerrit, Jira and word wrap support

This is a Git commit hook implemented in Java that will extend the commit message provided by the user in three ways:

* add the headline (e.g. "summary") of the Jira tickets referenced
* add a Change-Id for Gerrit
* wrap the text so that each line has at most 72 symbols

It can be easily extended or adapted.

## Gerrit Change-Id

The Change-Id footer line is required for [Gerrit](http://gerrit.googlesource.com/). It is generated randomly. Example:

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

## Jira tickets

Jira ticket IDs can be provided one per line at the beginning of the commit message. The hook will retrieve the headlines from Jira with the REST API and add them to the commit message.

Additionaly, the hook will add a "Jira-Id" footer line, so that is easy for other programs or script to find where issues have been fixed. For instance, Gerrit can use this information as "trackingid", so that searching for "bug:ABC-123" in the web interface will lead to the relevant commit.

### Single ticket

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

### Multiple tickets

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

## Word wrap

Initial commit message:

```
Lorem ipsum dolor sit amet, consectetuer adipiscing elit Aenean commodo ligula eget dolor Aenean massa.

Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.  Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a.

Venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi.
```

Output:

```
Lorem ipsum dolor sit amet, consectetuer adipiscing elit Aenean commodo ligula eget dolor Aenean massa.

Cum sociis natoque penatibus et magnis dis parturient montes, nascetur
ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu,
pretium quis, sem.  Nulla consequat massa quis enim. Donec pede justo,
fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo,
rhoncus ut, imperdiet a.

Venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium.
Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean
vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat
vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis,
feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.
Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue.
Curabitur ullamcorper ultricies nisi.
```

Furthermore, the hook will print a warning on the console:

```
WARNING: first line of commit message is longer than the recommended 72 characters
```

It will also print a warning if the second line is not empty:

```
WARNING: second line of commit message is not empty as it should be.
```

## Building the hook

The hook is provided in source code form only. It is designed to make adaptations and extensions easy.

You need in any case to build your own version containing the parameters of your Jira server. To build the hook, use Gradle:

```
gradlew  -PjiraUrl=http://replaceme.invalid  -PjiraUser=username  -PjiraPassword=password  createShellScript
```

This will generate the ```build/commit-msg``` shell script, which has two functions: extract the embedded JAR file on the first execution, and start the JAR on every execution. Put this script in your Git repository in the subdirectory ```.git/hooks```:

```
cp  build/commit-msg  <repopath>/.git/hooks/
```

The password is stored within the hook. You should use a dedicated, read-only Jira account. It needs the "Browse project" permission for all relevant projects.
