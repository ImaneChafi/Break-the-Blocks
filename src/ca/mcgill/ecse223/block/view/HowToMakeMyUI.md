# Background

Okay guys so I finished the UI framework as we discussed in tutorial on Wednesday.
It runs if you'd like to test it. Below are some instructions for clarity.

# How to setup your UI
Your UI will be nested into the UI framework called `Block223MainPage` as a `JPanel`.
However, I have written a superclass for you to make it easier for you to access the methods
which will change the UI. [This superclass extends JPanel so when you extend it your class
will also become a JPanel.]

## Steps
1. Create/Modify a class and extend `ContentPage` (`ContentPage` already extends JPanel).
2. Write your JPanel as you normally would

## Help methods that are inheirited
- **`createButton(String txt)`** this method will return a JButton that is formatted 
to follow the design style of the design style of the application.
- **`cancel()`** will return to the admin menu (for the Cancel button at the bottom of your UI)
- **`getSideMenuList()`** will return the JList which makes up the side menu.
Modifying this JList will display directly on the side menu.

## How to include your UI into `Block223MainPage` for testing
See lines 80-125 in `Block223MainPage`.
Under the case for your feature, add the line `displayedPage = new NameOfMyUIClass();`.
(As shown in the comments on lines 82, 85, and 88.)
[`displayPage` is the instance variable which holds the currently dispalyed page 
which is an object of `ContentPage`.]

# Notes
- I have not created methods to format/setup JTextField, JComboBox, or JSlider so if any of you want to
implement that please share.
- The *Save* and *Load* buttons currently do not do anything.
- If you want to see a page appear, change the default value of `currentPage` (`Block223MainPage`, line 26) to `Page.adminMenu`.