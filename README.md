# Peekaboo

Take screenshots from the terminal

Hopefully I'll make this useable with ssh but no promises

```
peekaboo [-o output_file] [-v] [-w]
```

---

## Goals:
- Take a screenshot
- Output an image file
- View the image file with your system's default image viewer
- Show a simple GUI that displays the image maybe

## How:
- Java using Robot and Swing
- Python using [PyAutoGUI](https://pyautogui.readthedocs.io/en/latest/) and [PyGame](https://www.pygame.org/) or [GTK](https://pygobject.readthedocs.io/en/latest/)
- Rust using [Scrap](https://crates.io/crates/scrap/0.5.0) and probably [Image](https://lib.rs/crates/image) and [GTK](https://gtk-rs.org/)

## Challenges:
- How this heck is gonna work with ssh
- I've literally never done anything in Rust before why am i using it help
