@echo off


REM Création d'un fichier sources.txt contenant les chemins vers chaque classe afin de tout compiler d'un coup
dir /s /B DungeonExit\src\*.java > sources.txt


REM Compilation des fichiers Java...
javac -d "bin" -cp "bin" @sources.txt


REM Suppression du fichier sources.txt
del sources.txt


REM Copie des ressources dans le dossier de sortie...
xcopy /E /I /Y /Q DungeonExit\res\* bin\ > nul


REM Lancement du programme Java...
echo.
java -cp "bin" main.Main