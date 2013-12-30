rem COPY  PEER%1

rem rmdir D:\BestPeerDemo\RUN\peer%1\classes /s /q
rem mkdir D:\BestPeerDemo\RUN\peer%1\classes
rem xcopy classes D:\BestPeerDemo\RUN\peer%1\classes /e /y /q
rem copy STORE\run_server_peer.bat  D:\BestPeerDemo\RUN\peer%1\classes
rem copy STORE\generate_localDb_data.bat  D:\BestPeerDemo\RUN\peer%1\classes
rem copy STORE\generate_metadata.bat  D:\BestPeerDemo\RUN\peer%1\classes
rem copy STORE\super%1.ini  D:\BestPeerDemo\RUN\peer%1\classes\conf\super.ini
copy STORE\localdbsample%1.sql  D:\BestPeerDemo\RUN\peer%1\classes\sqlscript\localdbsample.sql
rem copy STORE\serversqlcommand%1.sql  D:\BestPeerDemo\RUN\peer%1\classes\sqlscript\serversqlcommand.sql

rem pause