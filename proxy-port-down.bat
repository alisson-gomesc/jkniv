rem This batch file STOP an ip/port forward from localhost to vm (192.168.99.100)
rem https://docs.microsoft.com/en-us/previous-versions/windows/it-pro/windows-server-2008-R2-and-2008/cc731068(v=ws.10)?redirectedfrom=MSDN

netsh interface portproxy delete v4tov4 listenport=9042 listenaddress=127.0.0.1
netsh interface portproxy delete v4tov4 listenport=5432 listenaddress=127.0.0.1
netsh interface portproxy delete v4tov4 listenport=1521 listenaddress=127.0.0.1
netsh interface portproxy delete v4tov4 listenport=5984 listenaddress=127.0.0.1