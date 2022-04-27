/*

Subroutines and balancing aren't available in javascript
regex, so it's difficult to match tags properly.

The 'tag' pattern uses a limited recursive regex to match
up to three sets of angle brackets.

1: <[^><]*>
2: <(?:[^><]+|<[^><]*>)*>
3: <(?:[^><]+|<(?:[^><]+|<[^><]*>)*>)*>

With a depth of three, the following snippet would
only be highlighted at <cmd2 ...>.

<cmd1 <cmd2 <cmd3 <cmd4 ok!> ok!> ok!> nope!>
      └────────highlighted───────────┘

This is intended for highlighting simple example
code, so hopefully this won't be an issue, but
the pattern can be repeated to increase the limit
if needed.

--------

Inside 'tag', there's 'attr-value' which matches
from the first space until the second-to-last
character in the tag pattern. The last character
is always the closing '>', so this works well.

<command arg1 "arg 2" <command "is nested!!"]>>
│       └──────────attr-value────────────────┘│
└────tag──────────────────────────────────────┘

Then, the 'tag' pattern is checked again.

<command arg1 "arg 2" <command "is nested!!"]>>
                      │       └─attr-value──┘│
                      └────tag───────────────┘

This is recursive, but it's limited to up to three levels
of nested commands due to the regex issue noted above.

*/

Prism.languages.dialog = {
	'important': /^#.*/,
	'tag': {
		/* balanced angle bracket matching, up to 3 deep */
		pattern: /<(?:[^><]+|<(?:[^><]+|<[^><]*>)*>)*>/,
		inside: {
			'attr-value': {
				pattern: /\s[\s\S]*(?!$)/,
				inside: {
					//will be set for recursive parsing
					'tag': null
				}
			}
		}
	},
	'comment': {
		pattern: /\/\/.*\/\//,
		greedy: true
	}
};

Prism.languages.dialog['tag'].inside['attr-value'].inside['tag'] = Prism.languages.dialog['tag'];