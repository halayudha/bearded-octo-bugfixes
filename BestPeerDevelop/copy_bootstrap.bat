rem copy bootstrap
rmdir D:\BestPeerDemo\RUN\bootstrap\classes /s /q
mkdir D:\BestPeerDemo\RUN\bootstrap\classes
xcopy classes D:\BestPeerDemo\RUN\bootstrap\classes /e /y /q
copy STORE\run_bootstrap.bat  D:\BestPeerDemo\RUN\bootstrap\classes
copy STORE\generate_bootstrap_metadata.bat  D:\BestPeerDemo\RUN\bootstrap\classes
copy STORE\bootstrapsqlcommand.sql  D:\BestPeerDemo\RUN\bootstrap\classes\sqlscript\

rem pause