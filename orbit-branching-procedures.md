---
layout : page
title : "Orbit : Branching Procedures"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Policies](orbit-policies.html)"
next : "orbit-submission-policy.html"
previous: "orbit-coding-standards.html"
---
{% include JB/setup %}



-  [Always Branch](#BranchingProcedures-AlwaysBranch)
-  [Branch Naming](#BranchingProcedures-BranchNaming)
-  [Branch Build](#BranchingProcedures-BranchBuild)
-  [Pull Requests And Reviews](#BranchingProcedures-PullRequestsAndReviews)
-  [Delete Old Branches From Stash](#BranchingProcedures-DeleteOldBranchesFromStash)
-  [Tools](#BranchingProcedures-Tools)



This document is primarily aimed at internal Orbit developers but is shared publicly for transparency. 


Always Branch {#BranchingProcedures-AlwaysBranch}
----------


Avoid working in the master branch.


-  When collaborating with other developers create a feature branch.
-  When working in multiple divergent tasks create feature branches.
-  When working alone you can optionally use your personal branch.

For a deep dive on what a branch really is check: [What a Branch Is](http://git-scm.com/book/en/v1/Git-Branching-What-a-Branch-Is)


For a light read on branching GitHub style, check: [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/)


Branch Naming {#BranchingProcedures-BranchNaming}
----------


Personal branches:  /dev/yourusername  


-  Used for work that doesn't need collaboration. ex:  /dev/joe

Feature branches:  /feature/feature_or_taskname 


-  New feature called mpchar:  /feature/mpchar
-  Task to add telemetry to a bunch of services:  /feature/telementry

Branch Build {#BranchingProcedures-BranchBuild}
----------


The build process is configured to also build the branches. Push your branch to stash to trigger a build.


Push frequently to have frequent builds. 


Pull Requests And Reviews {#BranchingProcedures-PullRequestsAndReviews}
----------


Do frequent pull requests back to master to avoid big reviews.


Check the [submission policy](orbit-submission-policy.html).


Merge your branch with the latest version of master before creating a pull request.


Ensure that your branch passes the build before issuing a pull request.


Delete Old Branches From Stash {#BranchingProcedures-DeleteOldBranchesFromStash}
----------


Branches are useful during development and for release maintenance.


Delete old feature branches from stash.


Tools {#BranchingProcedures-Tools}
----------


-  [SourceTree](https://www.sourcetreeapp.com/): For push/pull, branching, and checkout.
-  IntelliJ: For creating local [changelists](https://www.jetbrains.com/idea/help/changelist.html), [shelving](https://www.jetbrains.com/idea/help/shelving-changes.html), and for [commits](https://www.jetbrains.com/idea/help/committing-changes-to-a-local-git-repository.html).
