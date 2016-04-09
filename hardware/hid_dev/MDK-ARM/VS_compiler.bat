@echo off
REM %1 - project name *.uvprojx, %2 - location of RAMdisc or another place to insert build logs
UV4.exe -b %1 -j0 -o %2\keil_build_out.txt
type %2\keil_build_out.txt