# DiapasonFX

![screenshot](/screenshots/diap_sshot.png)

This is a cross-platform desktop application that plays a sound of the selected note (every note from C4 to H4), with the extra ability to choose the pitch of A4, from 392 to 489Hz, in relation to which all notes transpose accordingly.

For non-Debian Linux users, the executable <code>.jar</code> file is continuously updated with every new commit. Donwload and run it with JRE 15 or higher. Debian-based Linux systems users can also use the <code>.deb</code> installer from releases.
Window and Mac OS users download from 'Releases' the file that corresponds to your Operating System and run it according to the release instructions.

For non-Debian Linux users (or any Linux user who prefers the <code>jar</code> executable over the <code>deb</code> one, I have included a <code>.desktop</code> file you can copy to your ~/.local/share/applications directory, to have com.dimitris47.diapasonfx.Diapason appear on your applications menu. You will need to download the <code>diapason.png</code> file from the <code>src/main/resources</code> folder and place it in the same directory as the <code>.jar</code> files. Just don't forget to edit it and replace "$JAR_DIRECTORY" in the 'exec' and 'icon' lines with the actual path of the files.
