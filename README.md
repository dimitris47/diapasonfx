# DiapasonFX

![screenshot](/screenshots/diap_sshot.png)

This is a UI update on the <a href="https://www.github.com/dimitris47/diapason">Diapason</a> project, based on JavaFX. Its functionality, though, remains the same. It is a portable cross-platform desktop application that plays a sound of the selected note (every note from C4 to H4), with the extra ability to choose the pitch of A4, from 392 to 489Hz, in relation to which all notes transpose accordingly.

Download and extract the latest release and then open the <code>DiapasonFX.jar</code> file with Java Runtime Environment (15 or higher). You might need to install the latest Java & JavaFX SDKs first. If this doesn't work for you, you can always use the <a href="https://github.com/dimitris47/diapason/releases/download/1.0/Diapason.jar">executable <code>jar</code></a> file from the <a href="https://www.github.com/dimitris47/diapason">Diapason</a> project.

For Linux users, I have included a <code>.desktop</code> file you can copy to your ~/.local/share/applications directory, to have Diapason appear on your applications menu. You will need to download the <code>diapason.png</code> file from the <code>res</code> folder and place it in the same directory as the <code>.jar</code> files. Just don't forget to edit it and replace "$JAR_DIRECTORY" in the 'exec' and 'icon' lines with the actual path of the files.
