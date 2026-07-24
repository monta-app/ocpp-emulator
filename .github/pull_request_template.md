<!-- Write the INTENT first, then make the diff match it. However this code was
     produced — hand-written, AI-assisted, or agent-generated — you own it, and
     the reviewer judges the diff against the intent you state here. State it
     clearly enough that "does the diff actually do this?" is answerable. Keep
     the description honest to the diff: claims of changes that aren't in the
     diff get rejected and merge slower. Cut the filler — anything better read
     in the code itself doesn't belong here.

     Be concise: a reviewer should get the gist in seconds. Length follows the
     change — a one-line fix gets a sentence, not five paragraphs. Plain prose,
     high signal, no padding. No filler openers ("This PR introduces…"), no
     line-by-line tour of the diff. Concise, not gutted: keep the what, the why,
     and how it was verified — trimming those is omission, not brevity. -->

# Intent

## What should this change accomplish?

_The goal in plain terms, then the acceptance criteria a reviewer can check the diff against — outcomes, not a line-by-line tour. Phrase each as something verifiable:_

- _Done when …_
- _Done when …_

## Scope / non-goals

_The intended blast radius: what this PR touches, and what it deliberately does **not** (tempting-but-out-of-scope changes left for later). This is the signal a reviewer can't get from the diff — and the cheapest guard against generated code wandering past its intent._

## Why?

_The motivation: the problem, the user/business need, the bug. Why now._

## How was it built and verified?

_Your attestation that you read the full diff and confirmed it does what the Intent says — you own this code regardless of how it was produced ("the model wrote it" is never a defense). Note what you ran (`./gradlew ktlintCheck`, `:common:jvmTest`) and what you verified manually in the running emulator (`./gradlew :v16:run`) — e.g. against which CSMS backend. Call out anything AI-generated that you reviewed, redirected, or rewrote._

_For UI changes, attach before/after screenshots or a short recording, and list the steps a reviewer can follow to see it themselves._

## Context

_Links to the Jira ticket (or `NOJIRA`), GitHub issue, Slack thread, or the relevant section of the OCPP 1.6 spec._
