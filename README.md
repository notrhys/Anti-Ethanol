
# Anti-Ethanol

Prevents some really badly designed SpigotMC malware from infecting your server


### How to setup
Simply download the agent jar from the releases tab

Drag and drop the agent into your main server directory

append -javaagent:Anti-Ethanol.jar to your server startup arguments

(Example: java -javaagent:Anti-Ethanol.jar -jar server.jar)

## Java 21+
If you run Java 21+ the security manager will not be able to monitor connections
i recommend you block the IP `84.252.120.172` from attempting to make any connections out from your server using IP-Tables

The agent will still be able to block the malware using ASM, so you don't really need to worry if you can't do this part.






#
SpigotMC doesn't really seem to be doing anything to combat this,
its quite easy to spot the malware in a decompiler, they don't even bother to hide it, for example see the screenshot below:
![image](https://github.com/user-attachments/assets/73a6a933-3a2f-4242-b877-6bc04b7858aa)
