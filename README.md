# Diapason

![screenshot](/screenshots/diapason_linux.png)

This is a portable cross-platform desktop application that plays a sound of the selected note (every note from C4 to H4), with the extra ability to choose the pitch of A4, from 392 to 489Hz, in relation to which all notes transpose accordingly.

You can use it simply by downloading and opening the <code>DiapasonFX.jar</code> file with Java Runtime Environment (15 or higher). You might need to have JDK installed beforehand in order to run the file.

There are no 'releases' of Diapason, as the <code>DiapasonFX.jar</code> file is updated with each commit that has changes in the code, making Diapason a kind of 'rolling' software.

For Linux users, I have included a <code>.desktop</code> file you can copy to your ~/.local/share/applications directory, to have Diapason appear on your applications menu. You will need to download the <code>diapason.png</code> file from the <code>res</code> folder and place it in the same directory as the <code>.jar</code> file. Just don't forget to edit it and replace "$JAR_DIRECTORY" in the 'exec' and 'icon' lines with the actual path of the files.
