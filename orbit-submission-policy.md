---
layout : page
title : "Orbit : Submission Policy"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Policies](orbit-policies.html)"
next : "orbit-acknowledgements.html"
previous: "orbit-branching-procedures.html"
---
{% include JB/setup %}



-  [Small and Independent Changelists](#SubmissionPolicy-SmallandIndependentChangelists)
-  [Useful Changelist Descriptions](#SubmissionPolicy-UsefulChangelistDescriptions)
-  [All Missing Logic Must Be Specified in TODOs](#SubmissionPolicy-AllMissingLogicMustBeSpecifiedinTODOs)
-  [All Changes Must Be Reviewed](#SubmissionPolicy-AllChangesMustBeReviewed)
-  [All Changes Must Have Functional Tests](#SubmissionPolicy-AllChangesMustHaveFunctionalTests)
-  [All Changes Must Be Tested](#SubmissionPolicy-AllChangesMustBeTested)
-  [Every Check-In Makes the Product Better (or no worse)](#SubmissionPolicy-EveryCheck-InMakestheProductBetter_ornoworse_)



This document is primarily aimed at internal Orbit developers but is shared publicly for transparency. 


Small and Independent Changelists {#SubmissionPolicy-SmallandIndependentChangelists}
----------


-  Changelists should be as small and independent as possible. Avoid multi-feature changelists. Ideally, you should be able to remove each changelist by itself.
-  Do not work for weeks at a time and then check in all your work. Account for the possibility that you might get called away unexpectedly and somebody will have to pick up your work. Check your work in incrementally. As a general guideline, a changelist should contain no more than three days worth of work, ideally one day.

Useful Changelist Descriptions {#SubmissionPolicy-UsefulChangelistDescriptions}
----------


-  All changelists should identify the task or the bug that corresponds to the work. They should also describe the actual work done. This is crucial when looking for bugs or managing integrations.

All Missing Logic Must Be Specified in TODOs {#SubmissionPolicy-AllMissingLogicMustBeSpecifiedinTODOs}
----------


-  Enforces a complete understanding of the feature.
-  Help reviewers identify what use cases were not covered by the author.
-  Easier to hand-over to other developers.

All Changes Must Be Reviewed {#SubmissionPolicy-AllChangesMustBeReviewed}
----------


-  All changelists must be reviewed via a pull request. 

All Changes Must Have Functional Tests {#SubmissionPolicy-AllChangesMustHaveFunctionalTests}
----------


-  Functional tests that exercise your code must accompany each check in

All Changes Must Be Tested {#SubmissionPolicy-AllChangesMustBeTested}
----------


-  You should manually exercise any tests that may be relevant

Every Check-In Makes the Product Better (or no worse) {#SubmissionPolicy-EveryCheck-InMakestheProductBetter_ornoworse_}
----------


-  You cannot check in any code that decreases the quality of the product.
-  In cases where you are replacing one system with another, it is preferable that either the replacement is equal to or greater than the system it is replacing, or that you leave both systems in place and allow users to switch to your replacement.

 

