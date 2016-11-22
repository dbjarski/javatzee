# Javatzee

A triple yahtzee game for up to four players.

### Notable Features
- Several options for die and lock styles
- Configurable die roll animation
- High scores table (select an entry to view the scorecard)
- History of all games played, in a sortable table (select an entry to view the scorecard)
- Undo button
- 1-click button to have the computer randomly populate all score boxes with random rolls (for demonstration purposes)

### Project Background
This is a program I worked on in my spare time during the early stages of my degree. The GUI was built using the Netbeans GUI Builder. The dice, bike locks, and emoji-me were drawn using the open-source Inkscape vector graphics editor.

I was a very novice programmer when I worked on much of the code, and all work on this program was completed before I learned software design patterns and principles, so don’t judge me too harshly for things like the user interface being tightly coupled (okay, super glued) to the business logic.

Games are saved to disk in a bit-packed binary file. Of course, I do have enough disk space that I don’t need to pack 39 numbers into 4 ints, but it was fun anyway. The bit-packing scheme is located in the main directory of the repository.
