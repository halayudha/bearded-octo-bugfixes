rem COPY  PEER%1

rmdir D:\BestPeerDemo\RUN\peer%1\classes /s /q
mkdir D:\BestPeerDemo\RUN\peer%1\classes
xcopy classes D:\BestPeerDemo\RUN\peer%1\classes /e /y /q
copy STORE\run_server_peer.bat  D:\BestPeerDemo\RUN\peer%1\classes
copy STORE\generate_localDb_data.bat  D:\BestPeerDemo\RUN\peer%1\classes
copy STORE\generate_metadata.bat  D:\BestPeerDemo\RUN\peer%1\classes
copy STORE\super%1.ini  D:\BestPeerDemo\RUN\peer%1\classes\conf\super.ini
copy STORE\localdbsample%1.sql  D:\BestPeerDemo\RUN\peer%1\classes\sqlscript\localdbsample.sql
copy STORE\serversqlcommand%1.sql  D:\BestPeerDemo\RUN\peer%1\classes\sqlscript\serversqlcommand.sql

rem pause