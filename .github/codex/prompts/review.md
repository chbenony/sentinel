You are reviewing a pull request. Run `git diff HEAD^1 HEAD` to see the PR's changes, where `HEAD` is the 
merge commit and `HEAD^1` is the base branch. Look at the surrounding code context and provide feedback.

Focus on:

- Correctness: logic errors, edge cases, off-by-one errors, race conditions
- Security: injection risks, unsafe handling of user input, exposed secrets
- Clarity: confusing naming, missing context that would help a reviewer
- Consistency: deviations from patterns already established elsewhere in the codebase

Running tests:

- The project is already built and its dependencies are cached. Run the test suite with `./gradlew test --offline --no-daemon`.
- When a change touches shared mutable state or concurrency, you may write and run a temporary test to probe for races (for example, many threads released together by a `CountDownLatch`). If you find a problem, cite the concrete evidence (inputs, thread count, observed vs expected result) in your comment.
- Treat test results as evidence, not as the whole review: passing tests do not prove the absence of races.

Do not comment on:

- Formatting or style that a linter would catch
- Personal preference where the existing code is already reasonable

Keep the review concise. If the change looks good, say so briefly instead of inventing nitpicks.

Report each issue as a separate entry in `comments`, anchored to the specific file and line it applies to (use the line number in the new/right-hand version of the file, and only lines that are actually part of the diff). Each comment body should explain the concrete problem and suggest a fix. Use `summary` for a short overall verdict (1-3 sentences) — do not repeat the per-line issues there. If there are no issues, return an empty `comments` array and say so in `summary`.
