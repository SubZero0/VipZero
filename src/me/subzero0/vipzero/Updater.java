package me.subzero0.vipzero;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
	private FileConfiguration l;
	private boolean br;
	private String version;
	private Main plugin;
	private boolean changelock=false;
	public Updater(Main main) {
		plugin=main;
	}
	public Updater(FileConfiguration language,boolean isBr) {
		l=language;
		br=isBr;
	}
	public Updater(String v) {
		version=v;
	}
	
	public String CheckNewVersion() throws Exception {
		String v = "";
		URL url = new URL("https://api.curseforge.com/servermods/files?projectIds=51620");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("User-Agent", "VipZero (by PauloABR)");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        JSONArray array = (JSONArray) JSONValue.parse(response);
        if (array.size() > 0) {
            JSONObject latest = (JSONObject) array.get(array.size() - 1);
            v = ((String) latest.get("name")).split("\\(")[1].split("\\)")[0].replace("V", "");
			/*
			// Get the version's link
			String versionLink = (String) latest.get(API_LINK_VALUE);                   - POSSO PEGAR O LINK
			// Get the version's release type
			String versionType = (String) latest.get(API_RELEASE_TYPE_VALUE);           - POSSO VER SE É RELEASE
			// Get the version's game version
			String versionGameVersion = (String) latest.get(API_GAME_VERSION_VALUE);    - POSSO PEGAR A VERSÃO
			*/
        }
		else
			return null;
		boolean f = false;
		if(!version.equals(v)) {
			String[] v_obtained = v.split("\\.");
			String[] v_here = version.split("\\.");
			
			boolean draw = true;
			for(int i=0;i<(v_obtained.length>v_here.length?v_here.length:v_obtained.length);i++) {
				int n_obtained = Integer.parseInt(v_obtained[i]);
				int n_here = Integer.parseInt(v_here[i]);
				
				if(n_obtained>n_here) {
					f=true;
					break;
				}
				if(n_obtained<n_here) {
					draw=false;
					break;
				}
			}
			
			if(draw&&v_obtained.length>v_here.length)
				f=true;
		}
		
		String r = (f?v:null);
		return r;
	}
	
	public int CheckConfigFile() {
		int c=0;
		if(!ccontains("MySQL.use"))
			c+=cfix("MySQL.use", false);
		if(!ccontains("MySQL.Host"))
			c+=cfix("MySQL.Host", "localhost");
		if(!ccontains("MySQL.Port"))
			c+=cfix("MySQL.Port", 3306);
		if(!ccontains("MySQL.Username"))
			c+=cfix("MySQL.Username", "test");
		if(!ccontains("MySQL.Password"))
			c+=cfix("MySQL.Password", "123");
		if(!ccontains("MySQL.Database"))
			c+=cfix("MySQL.Database", "minecraft");
		if(!ccontains("language"))
			c+=cfix("language", "en");
		if(!ccontains("server_name"))
			c+=cfix("server_name", "VipZero");
		if(!ccontains("check_for_updates"))
			c+=cfix("check_for_updates", true);
		if(!ccontains("case_sensitive_for_flatfile"))
			c+=cfix("case_sensitive_for_flatfile", false);
		if(!ccontains("key_length"))
			c+=cfix("key_length", 10);
		if(!ccontains("one_vip_change"))
			c+=cfix("one_vip_change", false);
		if(!ccontains("usekey_global"))
			c+=cfix("usekey_global", false);
		if(!ccontains("rvip_unlisted"))
			c+=cfix("rvip_unlisted", true);
		if(!ccontains("use_vault_for_permissions"))
			c+=cfix("use_vault_for_permissions", false);
		if(!ccontains("check_time"))
			c+=cfix("check_time", 10);
		if(!ccontains("pagseguro.use"))
			c+=cfix("pagseguro.use", false);
		if(!ccontains("pagseguro.email"))
			c+=cfix("pagseguro.email", "suporte@lojamodelo.com.br");
		if(!ccontains("pagseguro.token"))
			c+=cfix("pagseguro.token", "95112EE828D94278BD394E91C4388F20");
		if(!ccontains("pagseguro.mode"))
			c+=cfix("pagseguro.mode", 1);
		if(!ccontains("pagseguro.mysql_log"))
			c+=cfix("pagseguro.mysql_log", false);
		if(!ccontains("paypal.use"))
			c+=cfix("paypal.use", false);
		if(!ccontains("paypal.username"))
			c+=cfix("paypal.username", "username");
		if(!ccontains("paypal.password"))
			c+=cfix("paypal.password", "password");
		if(!ccontains("paypal.signature"))
			c+=cfix("paypal.signature", "signature");
		if(!ccontains("paypal.mode"))
			c+=cfix("paypal.mode", 1);
		if(!ccontains("paypal.mysql_log"))
			c+=cfix("paypal.mysql_log", false);
		if(!ccontains("logging.usekey"))
			c+=cfix("logging.usekey", false);
		if(!ccontains("vip_groups")) {
			List<String> l = new ArrayList<String>();
			l.add("vip1");
			l.add("vip2");
			c+=cfix("vip_groups", l);
		}
		if(!ccontains("default_group"))
			c+=cfix("default_group", "default");
		if(!ccontains("vip_items")) {
			List<String> l = new ArrayList<String>();
			l.add("vip1,0,264,1,none");
			l.add("vip1,0,35:3,1,none");
			l.add("vip1,0,35,1,none");
			l.add("vip1,0,potion:poison:true:10:1,1,blindness-10:1");
			l.add("vip1,0,$,1000,none");
			l.add("vip1,0,xp,10,none");
			l.add("vip1,0,cmd,say @player Muito bom!");
			l.add("vip1,0,310-311-312-313,1,protection-4");
			l.add("vip1,0,msg,Oi @player &3tudo bom&4?");
			l.add("vip,0,276,1,sharpness-3-knockback-2-name-&4Legendary @player-desc-&3desc 1-desc-&c@player");
			c+=cfix("vip_items", l);
		}
		return c;
	}
	
	private boolean ccontains(String n) {
		return plugin.getConfig().contains(n);
	}
	
	private int cfix(String n,Object n2) {
		plugin.getConfig().set(n, n2);
		return 1;
	}
	
	public int CheckOldConfigFile() {
		int c=0;
		if(plugin.getConfig().contains("plugin")) {
			for(String n : plugin.getConfig().getConfigurationSection("plugin").getKeys(true)) {
				plugin.getConfig().set(n, plugin.getConfig().get("plugin."+n));
				c++;
			}
			plugin.getConfig().set("plugin", null);
		}
		return c;
	}
	
	String atual_version = "1.2.2";
	public int CheckLanguageFile() {
		int c=0;
		if(!fcontains("version")) {
			c+=fix("version", atual_version);
			changelock=true;
		}
		else {
			String version = l.getString("version").trim();
			if(version.equalsIgnoreCase("1.1.2")||version.equalsIgnoreCase("1.1.3")||version.equalsIgnoreCase("1.1.4")||version.equalsIgnoreCase("1.2.0")||version.equalsIgnoreCase("1.2.1"))
				c+=fix("version", atual_version);
			else {
				//
			}
		}
		if(br) {
			if(!fcontains("expired"))
				c+=fix("expired", "Seu tempo de %group% expirou");
			if(!fcontains("message1"))
				c+=fix("message1", "dias de VIP");
			if(!fcontains("message2"))
				c+=fix("message2", "Dias dos VIPs");
			if(!fcontains("days"))
				c+=fix("days", "dias");
			if(!fcontains("list"))
				c+=fix("list", "Lista de keys");
			if(!fcontains("name"))
				c+=fix("name", "nome");
			if(!fcontains("rvip"))
				c+=fix("rvip", "O Admin %admin% retirou o VIP de %name%");
			if(!fcontains("cdays"))
				c+=fix("cdays", "O Admin %admin% mudou o %group% de %name% para %days% dias");
			if(!fcontains("group"))
				c+=fix("group", "grupo");
			if(!fcontains("reload"))
				c+=fix("reload", "Configuracao recarregada");
			if(!fcontains("initialdate"))
				c+=fix("initialdate", "Data inicial");
			if(!fcontains("daysleft"))
				c+=fix("daysleft", "Dias restantes");
			if(!fcontains("success1"))
				c+=fix("success1", "Keys apagadas");
			if(!fcontains("success2"))
				c+=fix("success2", "Adquirido %group% por %days% dias com sucesso");
			if(!fcontains("success3"))
				c+=fix("success3", "%name% adquiriu seu %group% por %days% dias com sucesso");
			if(!fcontains("success4"))
				c+=fix("success4", "Grupo VIP mudado");
			if(!fcontains("success5"))
				c+=fix("success5", "Key %key% apagada");
			if(!fcontains("error1"))
				c+=fix("error1", "Os dias precisam ser maiores que 0");
			if(!fcontains("error2"))
				c+=fix("error2", "DIAS precisa ser um numero");
			if(!fcontains("error3"))
				c+=fix("error3", "Nao ha keys");
			if(!fcontains("error4"))
				c+=fix("error4", "Nao ha keys, crie com /gerarkey");
			if(!fcontains("error5"))
				c+=fix("error5", "Key nao encontrada");
			if(!fcontains("error6"))
				c+=fix("error6", "Voce nao e um VIP");
			if(!fcontains("error7"))
				c+=fix("error7", "Jogador nao encontrado");
			if(!fcontains("error8"))
				c+=fix("error8", "Grupo nao encontrado");
			if(!fcontains("error9"))
				c+=fix("error9", "nao e um VIP");
			if(!fcontains("error10"))
				c+=fix("error10", "Apenas uma troca de VIP por dia");
			if(!fcontains("error11"))
				c+=fix("error11", "Voce nao tem permissao");
			if(!fcontains("error12"))
				c+=fix("error12", "Voce nao tem esse tipo de VIP");
			if(!fcontains("error13"))
				c+=fix("error13", "Este codigo esta sendo processado");
			if(!fcontains("addvip"))
				c+=fix("addvip", "Adicionado %days% dias para todos do grupo %group%");
			if(!fcontains("error14"))
				c+=fix("error14", "Este codigo ja foi usado");
			if(!fcontains("error15"))
				c+=fix("error15", "Codigo nao encontrado");
			if(!fcontains("error16"))
				c+=fix("error16", "Este codigo nao foi pago");
			if(!fcontains("error17"))
				c+=fix("error17", "Nenhum pagamento de VIP encontrado neste codigo");
			if(!fcontains("error18"))
				c+=fix("error18", "Um erro ocorreu ao validar seu pedido");
		}
		else {
			if(!fcontains("expired"))
				c+=fix("expired", "Your time with %group% expired");
			if(!fcontains("message1"))
				c+=fix("message1", "days with VIP");
			if(!fcontains("message2"))
				c+=fix("message2", "Days with VIP");
			if(!fcontains("days"))
				c+=fix("days", "days");
			if(!fcontains("list"))
				c+=fix("list", "List of keys");
			if(!fcontains("name"))
				c+=fix("name", "name");
			if(!fcontains("rvip"))
				c+=fix("rvip", "The Admin %admin% removed the VIP from %name%");
			if(!fcontains("cdays"))
				c+=fix("cdays", "The Admin %admin% changed the %group% of %name% to %days% days");
			if(!fcontains("group"))
				c+=fix("group", "group");
			if(!fcontains("reload"))
				c+=fix("reload", "Config reloaded");
			if(!fcontains("initialdate"))
				c+=fix("initialdate", "Initial date");
			if(!fcontains("daysleft"))
				c+=fix("daysleft", "Days left");
			if(!fcontains("success1"))
				c+=fix("success1", "All keys deleted");
			if(!fcontains("success2"))
				c+=fix("success2", "Acquired %group% with %days% days successfully");
			if(!fcontains("success3"))
				c+=fix("success3", "%name% acquired his %group% for %days% days successfully");
			if(!fcontains("success4"))
				c+=fix("success4", "VIP group changed");
			if(!fcontains("success5"))
				c+=fix("success5", "Key %key% deleted");
			if(!fcontains("error1"))
				c+=fix("error1", "The days need to be more than 0");
			if(!fcontains("error2"))
				c+=fix("error2", "DAYS need to be a number");
			if(!fcontains("error3"))
				c+=fix("error3", "No keys found");
			if(!fcontains("error4"))
				c+=fix("error4", "No keys found, create with /newkey");
			if(!fcontains("error5"))
				c+=fix("error5", "Key not found");
			if(!fcontains("error6"))
				c+=fix("error6", "You are not a VIP");
			if(!fcontains("error7"))
				c+=fix("error7", "Player not found");
			if(!fcontains("error8"))
				c+=fix("error8", "Group not found");
			if(!fcontains("error9"))
				c+=fix("error9", "is not a VIP");
			if(!fcontains("error10"))
				c+=fix("error10", "Only one change per day");
			if(!fcontains("error11"))
				c+=fix("error11", "You dont have permission");
			if(!fcontains("error12"))
				c+=fix("error12", "You dont have this type of VIP");
			if(!fcontains("error13"))
				c+=fix("error13", "This code is being processed");
			if(!fcontains("addvip"))
				c+=fix("addvip", "Added %days% days to all from group %group%");
			if(!fcontains("error14"))
				c+=fix("error14", "This code has already been used");
			if(!fcontains("error15"))
				c+=fix("error15", "Code not found");
			if(!fcontains("error16"))
				c+=fix("error16", "This code was not paid");
			if(!fcontains("error17"))
				c+=fix("error17", "No VIP payment found on this code");
			if(!fcontains("error18"))
				c+=fix("error18", "An error occurred when validating your request");
		}
		return c;
	}
	
	private boolean fcontains(String n) {
		if(changelock)
			return false;
		return l.contains(n);
	}
	
	private int fix(String n,Object n2) {
		l.set(n,n2);
		return 1;
	}
}
