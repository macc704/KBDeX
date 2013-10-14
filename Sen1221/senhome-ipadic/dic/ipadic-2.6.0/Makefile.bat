REM  
REM  Makefile.bat ipadic-2.5.1
REM
@echo off
@echo Makefile.bat for ipadic-2.5.1

cd dic

@echo copy...
copy connect.cha _connect.c
if errorlevel 1 goto ERROREXIT

copy _connect.c _connect.cha
REM cl -E _connect.c > _connect.cha

@echo makemat...
..\mkchadic\makemat
if errorlevel 1 goto ERROREXIT

del _connect.c
del _connect.cha

@echo makeint...
..\mkchadic\makeint -o chadic.txt *.dic
if errorlevel 1 goto ERROREXIT

@echo makeda...
..\mkchadic\makeda chadic.txt chadic
if errorlevel 1 goto ERROREXIT

del chadic.txt

cd ..

@echo chasen dictionary compiled successfully.
goto LAST

:ERROREXIT
@echo cannot make chasen dictionary.

:LAST
@echo on
