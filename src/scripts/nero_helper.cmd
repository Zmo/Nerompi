@echo off

rem Tämä skripti vaatii sen, että se ajetaan cmd.exe:n alta käyttäen /v parametria.
rem Siksi tuossa on tuo nero.cmd, joka kannattaa ajaa.
rem jos et halua teksti-ikkunaa auki vaihda alta kommentit toiseen riviin...


set nerocp="./

for /f "delims=" %%x in ('dir /a-d /b *.jar') do set nerocp=!nerocp!;%%x
for /f "delims=" %%x in ('dir /a-d /b lib\*.jar') do set nerocp=!nerocp!;lib/%%x
for /f "delims=" %%x in ('dir /a-d /b lib\batik\*.jar') do set nerocp=!nerocp!;lib/batik/%%x

set nerocp=%nerocp%"

java -classpath %nerocp% fi.helsinki.cs.nero.NeroApplication %1 %2 %3 %4 %5 %6 %7 %8 %9
rem start javaw -classpath %nerocp% fi.helsinki.cs.nero.NeroApplication %1 %2 %3 %4 %5 %6 %7 %8 %9

echo Paina n„pp„int„ sulkeaksesi t„m„n ikkunan...
pause > nul
