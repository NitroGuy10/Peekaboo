# Peekaboo

Take screenshots from the terminal

Yep, that's it

```
java peekaboo output_file [-no] [-v] [-nc]

output_file  .... Output an image file to the specified path (WILL OVERWRITE BY DEFAULT)
-no          .... Do not overwrite if output_file already exists
-v           .... Open the resultant image file in the default image viewer
-nc          .... Do not attempt to crop the screenshot (see "Known Quirks" for more details)
```

Written in Java

---

## Known Quirks:
If multiple screens are being used,
Peekaboo will include all screens in the same image.
However, it can be exceptionally difficult to determine the position of those screens based on the user's setup.

Peekaboo's solution to this is to overestimate the dimensions of the screenshot,
and then crop it afterwards. The cropping process involves looking for sections of all black pixels and,
as you can probably imagine, this could potentially lead to over-cropping if entire rows or columns of black pixels exist on the edges of one's screen(s).

If you're worried about potentially incorrect cropping,
use the -nc flag to disable cropping and generate an image with the excess blank space still there.
