CONNECT TO YanDB USER ircm_yanusheusky USING Zydfhm129;

LOAD FROM "D:\wsp\XMLParser\ASFKI_Parser1\asfki_db2_files\h_strana.txt" OF DEL modified by nochardel coldel0x7c MESSAGES "D:\logs\h_strana_log.txt" REPLACE INTO IA00.h_strana;

LOAD FROM "D:\wsp\XMLParser\ASFKI_Parser1\asfki_db2_files\strana.txt" OF DEL modified by nochardel coldel0x7c MESSAGES "D:\logs\strana_log.txt" REPLACE INTO IA00.strana;