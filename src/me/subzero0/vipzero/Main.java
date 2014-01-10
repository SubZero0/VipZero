package me.subzero0.vipzero;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.subzero0.fullpvp.permissoes.Permissoes;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener {
	protected GMHook hook = new GMHook(this);
	protected static Economy econ = null;
	protected static Permission perms = null;
	protected boolean block_dinheiro = false;
	protected FileConfiguration language = null;
    protected boolean flatfile = true;
    protected boolean usekey_global = false;
    protected String need_update = null;
    protected boolean use_vault_for_perms = true;
    protected HashMap<String, String> trocou = new HashMap<String, String>();
    
    //Mysql
    protected String mysql_url = "";
    protected String mysql_user = "";
    protected String mysql_pass = "";
    protected HashMap<String,String> using_codes = new HashMap<String,String>();
    
    //PagSeguro
    protected boolean use_pagseguro = false;
    protected boolean mysql_pagseguro = false;
    protected HashMap<String,String> using_ps = new HashMap<String,String>();
    protected FileConfiguration pagseguro = null;
    
	//PayPal
    protected boolean use_paypal = false;
    protected boolean mysql_paypal = false;
    protected HashMap<String,String> using_pp = new HashMap<String,String>();
    protected FileConfiguration paypal = null;
    
	
	@EventHandler
	public void onPluginEnabled(PluginEnableEvent event) {
        hook.onPluginEnable(event);
    }
	
	@EventHandler
	public void onPluginDisabled(PluginDisableEvent event) {
        hook.onPluginDisable(event);
    }
	
	@Override
    public void onEnable() {
		getLogger().info("Enabling VipZero (V"+getDescription().getVersion()+") - Author: SubZero0");
		Calendar cal = Calendar.getInstance();
		if(cal.get(Calendar.DATE)==25&&cal.get(Calendar.MONTH)==11) {
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA+"============ [ VIP ZERO ] ============");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"I wish you a merry Christmas!");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"Thanks for using the plugin!");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"By SubZero0");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA+"======================================");
		}
		if(cal.get(Calendar.DATE)==1&&cal.get(Calendar.MONTH)==0) {
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA+"============ [ VIP ZERO ] ============");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"I wish you a happy New Year!");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"Thanks for using the plugin!");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"By SubZero0");
			getServer().getConsoleSender().sendMessage("");
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA+"======================================");
		}
		
		boolean check_update = true;
		if(getConfig().contains("check_for_updates"))
			if(!getConfig().getBoolean("check_for_updates"))
				check_update=false;
		if(check_update) {
			getLogger().info("Checking for updates...");
			try {
				Updater vup = new Updater(getDescription().getVersion());
				String vup_r = vup.CheckNewVersion();
				if(vup_r==null)
					getLogger().info("No updates found.");
				else {
					getLogger().info("New update avaible: V"+vup_r+"!");
					getLogger().info("Download: http://dev.bukkit.org/server-mods/vipzero/");
					need_update=vup_r;
				}
			}
			catch(Exception e) {
				getLogger().info("Error when checking for updates!");
				e.printStackTrace();
			}
		}
		
		getServer().getPluginCommand("gerarkey").setExecutor(new Commands(this));
		getServer().getPluginCommand("newkey").setExecutor(new Commands(this));
		getServer().getPluginCommand("keys").setExecutor(new Commands(this));
		getServer().getPluginCommand("apagarkeys").setExecutor(new Commands(this));
		getServer().getPluginCommand("delkeys").setExecutor(new Commands(this));
		getServer().getPluginCommand("apagarkey").setExecutor(new Commands(this));
		getServer().getPluginCommand("delkey").setExecutor(new Commands(this));
		getServer().getPluginCommand("usarkey").setExecutor(new Commands(this));
		getServer().getPluginCommand("usekey").setExecutor(new Commands(this));
		getServer().getPluginCommand("tempovip").setExecutor(new Commands(this));
		getServer().getPluginCommand("viptime").setExecutor(new Commands(this));
		getServer().getPluginCommand("vipzero").setExecutor(new Commands(this));
		getServer().getPluginCommand("tirarvip").setExecutor(new Commands(this));
		getServer().getPluginCommand("rvip").setExecutor(new Commands(this));
		getServer().getPluginCommand("trocarvip").setExecutor(new Commands(this));
		getServer().getPluginCommand("changevip").setExecutor(new Commands(this));
		getServer().getPluginCommand("mudardias").setExecutor(new Commands(this));
		getServer().getPluginCommand("changedays").setExecutor(new Commands(this));
		getServer().getPluginCommand("darvip").setExecutor(new Commands(this));
		getServer().getPluginCommand("givevip").setExecutor(new Commands(this));
		getServer().getPluginCommand("addvip").setExecutor(new Commands(this));
		getServer().getPluginManager().registerEvents(this, this);
		
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}
			catch(Exception e) {}
		}
		else {			
			getLogger().info("Checking for old config file...");
			Updater ocup = new Updater(this);
			int ocup_r = ocup.CheckOldConfigFile();
			if(ocup_r==0)
				getLogger().info("No updates found.");
			else {
				getLogger().info(ocup_r+" updates found! Config file updated!");
				saveConfig();
			}
			
			getLogger().info("Checking for config file update...");
			Updater cup = new Updater(this);
			int cup_r = cup.CheckConfigFile();
			if(cup_r==0)
				getLogger().info("No updates found.");
			else {
				getLogger().info(cup_r+" updates found and added!");
				saveConfig();
			}
		}
		reloadConfig();
		try {File file2 = new File(getDataFolder(),"language_br.yml");if(!file2.exists()) {saveResource("language_br.yml",false);getLogger().info("Saved language_br.yml");}}
		catch(Exception e) {}
		try {File file2 = new File(getDataFolder(),"language_en.yml");if(!file2.exists()) {saveResource("language_en.yml",false);getLogger().info("Saved language_en.yml");}}
		catch(Exception e) {}
		
		File lFile = new File(this.getDataFolder(), "language_"+getConfig().getString("language").trim()+".yml");
		language = YamlConfiguration.loadConfiguration(lFile);
		getLogger().info("Checking for language file update...");
		Updater lup = new Updater(language,(getConfig().getString("language").trim().equalsIgnoreCase("br")?true:false));
		int lup_r = lup.CheckLanguageFile();
		if(lup_r==0)
			getLogger().info("No updates found.");
		else {
			getLogger().info(lup_r+" updates found and added!");
			try {
				language.save(lFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(getConfig().getBoolean("MySQL.use")) {
			mysql_url = "jdbc:mysql://"+getConfig().getString("MySQL.Host").trim()+":"+getConfig().getInt("MySQL.Port")+"/"+getConfig().getString("MySQL.Database").trim()+"";
			mysql_user = getConfig().getString("MySQL.Username").trim();
			mysql_pass = getConfig().getString("MySQL.Password").trim();
			try {
				Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
				flatfile = false;
				if (con == null) {
					getLogger().info("Connection to MySQL failed! Changing to flatfile.");
					flatfile = true;
				}
				else {
					getLogger().info("Connected to MySQL server!");
					Statement st = con.createStatement();
					st.execute("CREATE TABLE IF NOT EXISTS `keys` (`key` VARCHAR(11) PRIMARY KEY, `grupo` VARCHAR(15), `dias` INT);");
					st.execute("CREATE TABLE IF NOT EXISTS `vips` (`nome` VARCHAR(30) PRIMARY KEY, `inicio` VARCHAR(11), `usando` VARCHAR(15));");
					for(String gname : getConfig().getStringList("vip_groups")) {
						try {
							PreparedStatement pst2 = con.prepareStatement("ALTER TABLE `vips` ADD COLUMN `"+gname.trim()+"` VARCHAR(15) NOT NULL DEFAULT 0;");
							pst2.execute();
							pst2.close();
						}
						catch(SQLException e) {}
					}
					if(getConfig().getBoolean("logging.usekey"))
						st.execute("CREATE TABLE IF NOT EXISTS `vipzero_log` (`comando` VARCHAR(20), `nome` VARCHAR(30), `key` VARCHAR(11) PRIMARY KEY, `data` VARCHAR(11), `grupo` VARCHAR(15), `dias` INT);");
					st.close();
				}
				con.close();
			}
			catch (SQLException e) {
				getLogger().warning("Connection to MySQL failed! Changing to flatfile.");
				e.printStackTrace();
				flatfile=true;
			}
		}
		else
			getLogger().info("Using flatfile system.");
		
		if(flatfile&&getConfig().getBoolean("logging.usekey")) {
			try {File file2 = new File(getDataFolder(),"log.txt");if(!file2.exists()) {saveResource("log.txt",false);getLogger().info("Saved log.txt");}}
			catch(Exception e) {}
		}
		
		usekey_global = getConfig().getBoolean("usekey_global");
		
		int tempo = getConfig().getInt("check_time");
        if(tempo!=0) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				public void run() {
					for(Player p : getServer().getOnlinePlayers()) {
						AtualizarVIP(p);
					}
	            }
	        }, 20L, 1200*tempo);
        }
        
        use_vault_for_perms=getConfig().getBoolean("use_vault_for_permissions");
        if(use_vault_for_perms)
        	getLogger().info("Use only Vault for permissions: ENABLED!");
		
        boolean perm_linked=false;
		if(getServer().getPluginManager().getPlugin("GroupManager")!=null&&!use_vault_for_perms) {
    		getLogger().info("Hooked to GroupManager.");
    		perm_linked=true;
		}
    	else if(getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!use_vault_for_perms) {
    		getLogger().info("Hooked to PermissionsEx.");
    		perm_linked=true;
    	}
    	else if(getServer().getPluginManager().getPlugin("FullPVP")!=null&&!use_vault_for_perms) {
    		getLogger().info("Hooked to FullPVP.");
    		perm_linked=true;
    	}
    	else {
    		if (!setupPermissions() ) {
    			getLogger().warning("ERROR: No permissions plugin found! Disabling...");
        		getServer().getPluginManager().disablePlugin(this);
            }
    		else {
    			getLogger().info("Hooked to Vault (Permission).");
    			perm_linked=true;
    		}
    	}
		
		if(perm_linked) {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
    			getLogger().warning("WARNING: Plugin Vault not found. Transfers disabled.");
    			block_dinheiro=true;
            }
    		else {
	    		if (!setupEconomy()) {
	    			getLogger().warning("WARNING: Vault is not linked to any economy plugin. Transfers disabled.");
	    			block_dinheiro=true;
	            }
	    		else {
	    			setupPermissions();
	    	        getLogger().info("Hooked to Vault (Economy).");
	    		}
    		}
		}
		
		if(getConfig().getBoolean("pagseguro.use")) {
			if(!getConfig().getString("pagseguro.email").equals("suporte@lojamodelo.com.br")&&!getConfig().getString("pagseguro.token").equals("95112EE828D94278BD394E91C4388F20"))
				use_pagseguro=true;
		}
		
		if(use_pagseguro) {
			if (getServer().getPluginManager().getPlugin("PagSeguro API") == null) {
				getLogger().warning("PagSeguro API not found!");
			}
			else {
				getLogger().info("PagSeguro enabled!");
				
				try {File file2 = new File(getDataFolder(),"pagseguro.yml");if(!file2.exists()) {saveResource("pagseguro.yml",false);getLogger().info("Saved pagseguro.yml");}}
				catch(Exception e) {}
					
				File lFile3 = new File(this.getDataFolder(), "pagseguro.yml");
				pagseguro = YamlConfiguration.loadConfiguration(lFile3);
				
				if(!flatfile) {
					mysql_pagseguro = getConfig().getBoolean("pagseguro.mysql_log");
					if(mysql_pagseguro) {
						try {
							Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
							Statement st = con.createStatement();
							st.execute("CREATE TABLE IF NOT EXISTS `vipzero_pagseguro` (`key` VARCHAR(45) PRIMARY KEY, `nome` VARCHAR(30), `data` VARCHAR(11));");
							st.close();
							con.close();
						} catch(Exception e) {e.printStackTrace();}
					}
				}
			}
		}
		
		if(getConfig().getBoolean("paypal.use")) {
			if(!getConfig().getString("paypal.username").equals("username")&&!getConfig().getString("paypal.password").equals("password")&&!getConfig().getString("paypal.signature").equals("signature"))
				use_paypal=true;
		}
		
		if(use_paypal) {
			if (getServer().getPluginManager().getPlugin("PayPal NVP") == null) {
				getLogger().warning("PayPal NVP not found!");
			}
			else {
				getLogger().info("PayPal enabled!");
				
				try {File file2 = new File(getDataFolder(),"paypal.yml");if(!file2.exists()) {saveResource("paypal.yml",false);getLogger().info("Saved paypal.yml");}}
				catch(Exception e) {}
					
				File lFile3 = new File(this.getDataFolder(), "paypal.yml");
				paypal = YamlConfiguration.loadConfiguration(lFile3);
				
				if(!flatfile) {
					mysql_paypal = getConfig().getBoolean("paypal.mysql_log");
					if(mysql_paypal) {
						try {
							Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
							Statement st = con.createStatement();
							st.execute("CREATE TABLE IF NOT EXISTS `vipzero_paypal` (`key` VARCHAR(20) PRIMARY KEY, `nome` VARCHAR(30), `data` VARCHAR(11));");
							st.close();
							con.close();
						} catch(Exception e) {e.printStackTrace();}
					}
				}
			}
		}
    }
 
    @Override
    public void onDisable() {
    	getLogger().info("Disabling VipZero - Author: SubZero0");
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }
    
    protected void removeRelatedVipGroups(Player p) {
    	if(getServer().getPluginManager().getPlugin("GroupManager")!=null&&!use_vault_for_perms) {
    		//n achei como controlar os subgroups
    	}
    	else if(getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!use_vault_for_perms) {
    		PermissionUser user  = PermissionsEx.getUser(p);
    		for(String g : user.getGroupsNames()) {
    			boolean existe = false;
    			for(String list : getConfig().getStringList("vip_groups"))
    				if(g.equalsIgnoreCase(list)) {
        				existe=true;
        				break;
        			}
    			if(existe)
    				user.removeGroup(g);
    		}
    	}
    	else {
    		for(String g : perms.getPlayerGroups(p)) {
    			boolean existe = false;
    			for(String list : getConfig().getStringList("vip_groups"))
    				if(g.equalsIgnoreCase(list)) {
        				existe=true;
        				break;
        			}
    			if(existe)
    				perms.playerRemoveGroup(p, g);
    		}
    	}
    }
    
    protected void DarVip(Player p, int dias, String grupo) {
    	if(getServer().getPluginManager().getPlugin("GroupManager")!=null&&!use_vault_for_perms) {
    		boolean temvip=false;
			for(String list : getConfig().getStringList("vip_groups"))
    			if(hook.getGroup(p).equalsIgnoreCase(list)) {
    				temvip=true;
    				break;
    			}
			if(!temvip) {
				removeRelatedVipGroups(p);
				hook.setGroup(p,grupo);
				if(flatfile) {
					getConfig().set("vips."+getRealName(p.getName())+".usando",grupo);
					saveConfig();
				}
				else {
					ThreadVZ t = new ThreadVZ(this,"darvip",p,grupo);
					t.start();
				}
			}
    	}
    	else if(getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!use_vault_for_perms) {
    		PermissionUser user  = PermissionsEx.getUser(p);
    		boolean temvip=false;
			for(String list : getConfig().getStringList("vip_groups"))
				for(String gName : user.getGroupsNames())
	    			if(list.equalsIgnoreCase(gName)) {
	    				temvip=true;
	    				break;
	    			}
			if(!temvip) {
				removeRelatedVipGroups(p);
	    		user.addGroup(grupo);
	    		if(flatfile) {
	    			getConfig().set("vips."+getRealName(p.getName())+".usando",grupo);
					saveConfig();
	    		}
				else {
					ThreadVZ t = new ThreadVZ(this,"darvip",p,grupo);
					t.start();
				}
			}
    	}
    	else if(getServer().getPluginManager().getPlugin("FullPVP")!=null&&!use_vault_for_perms) {
    		Permissoes user = new Permissoes(p);
    		if(!user.hasPermission("fullpvp.vip")) {
    			user.addPermission("fullpvp.vip");
    			if(flatfile) {
					getConfig().set("vips."+getRealName(p.getName())+".usando",grupo);
					saveConfig();
				}
				else {
					ThreadVZ t = new ThreadVZ(this,"darvip",p,grupo);
					t.start();
				}
    		}
    	}
    	else {
    		boolean temvip=false;
			for(String list : getConfig().getStringList("vip_groups"))
				for(String gName : perms.getPlayerGroups(p))
	    			if(list.equalsIgnoreCase(gName)) {
	    				temvip=true;
	    				break;
	    			}
			if(!temvip) {
				removeRelatedVipGroups(p);
				perms.playerAddGroup(p,grupo);
				if(flatfile) {
					getConfig().set("vips."+getRealName(p.getName())+".usando",grupo);
					saveConfig();
				}
				else {
					ThreadVZ t = new ThreadVZ(this,"darvip",p,grupo);
					t.start();
				}
			}
    	}
    	DarItensVip(p, dias, grupo);
    }
    
    protected void TirarVip(final Player p, final String grupo, String fGrupo) {
    	String gFinal;
    	if(fGrupo==null)
    		gFinal = getConfig().getString("default_group").trim();
    	else
    		gFinal = fGrupo;
    	if(flatfile) {
    		getConfig().set("vips."+getRealName(p.getName())+"."+grupo, null);
    		if(fGrupo==null)
    			getConfig().set("vips."+getRealName(p.getName()),null);
    		else
    			getConfig().set("vips."+getRealName(p.getName())+".usando",fGrupo);
    		saveConfig();
    	}
    	else {
    		ThreadVZ t = new ThreadVZ(this,"tirarvip",p,grupo,fGrupo);
    		t.start();
    	}
    	if(getServer().getPluginManager().getPlugin("GroupManager")!=null&&!use_vault_for_perms) {
    		hook.setGroup(p, gFinal);
    	}
    	else if(getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!use_vault_for_perms) {
    		PermissionUser user  = PermissionsEx.getUser(p);
    		removeRelatedVipGroups(p);
    		user.addGroup(gFinal);
    	}
    	else if(getServer().getPluginManager().getPlugin("FullPVP")!=null&&!use_vault_for_perms) {
    		Permissoes user = new Permissoes(p);
    		user.removePermission("fullpvp.vip");
    	}
    	else {
    		perms.playerRemoveGroup(p, grupo);
    		removeRelatedVipGroups(p);
    		perms.playerAddGroup(p, gFinal);
    	}
    	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    @Override 
		    public void run() {
		    	p.sendMessage(ChatColor.AQUA+"["+getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+getMessage("expired").trim().replaceAll("%group%", grupo.toUpperCase())+".");
		    }
		}, 80L);
    }
    
    protected void TirarVip2(final Player p, final String grupo) {
    	if(flatfile) {
    		getConfig().set("vips."+getRealName(p.getName())+"."+grupo, null);
    		saveConfig();
    	}
    	else {
    		ThreadVZ t = new ThreadVZ(this,grupo,"tirarvip2",p);
    		t.start();
    	}
    	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    @Override 
		    public void run() {
		    	p.sendMessage(ChatColor.AQUA+"["+getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+getMessage("expired").trim().replaceAll("%group%", grupo.toUpperCase())+".");
		    }
		}, 80L);
    }
    
    protected void DarItensVip(Player p, int dias, String group) {
    	Items i = new Items(this,p,dias,group);
    	i.start();
    }
    
    @EventHandler
    protected void onLogin(PlayerJoinEvent e) {
    	AtualizarVIP(e.getPlayer());
    	if(e.getPlayer().hasPermission("vipzero.notify")||e.getPlayer().hasPermission("vipzero.notificar")) {
    		final Player p = e.getPlayer();
    		getServer().getScheduler().runTaskLater(this, new Runnable() {
    			public void run() {
    				if(need_update!=null) {
    					p.sendMessage(ChatColor.AQUA+"[VipZero] "+ChatColor.WHITE+"New update avaible: "+ChatColor.AQUA+"V"+need_update+"!");
    					p.sendMessage(ChatColor.AQUA+"Download: "+ChatColor.WHITE+"http://dev.bukkit.org/server-mods/vipzero/");
    				}
    			}
    		}, 60L);
    	}
    }

	protected String getMessage(String t) {
    	return language.getString(t).trim();
    }
    
    protected String getLanguage() {
    	return getConfig().getString("language").trim();
    }

	public void AtualizarVIP(Player p) {
		if(flatfile) {
		   	if(getConfig().contains("vips."+getRealName(p.getName()))) {
		    	Calendar now = Calendar.getInstance();
				Calendar vip = Calendar.getInstance();
				Calendar vip_fixo = Calendar.getInstance();
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				String data = getConfig().getString("vips."+getRealName(p.getName())+".inicio").trim();
				String usando = getConfig().getString("vips."+getRealName(p.getName())+".usando").trim();
				int dias = getConfig().getInt("vips."+getRealName(p.getName())+"."+usando);
				try {
					vip.setTime(fmt.parse(data));
					vip_fixo.setTime(fmt.parse(data));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				if(!fmt.format(vip.getTime()).equals(fmt.format(now.getTime()))) {
					vip.add(Calendar.DATE, dias);
					if(now.after(vip)) {
						Calendar vip2 = Calendar.getInstance();
						vip2.setTime(vip.getTime());
						Calendar temp = Calendar.getInstance();
						temp.setTime(vip.getTime());
						String fim = null;
						for(String n : getConfig().getStringList("vip_groups"))
							if(!n.equalsIgnoreCase(usando)) {
				    			if(getConfig().contains("vips."+getRealName(p.getName())+"."+n.trim())) {
					    			vip2.add(Calendar.DATE, getConfig().getInt("vips."+getRealName(p.getName())+"."+n.trim()));
				    				if(now.after(vip2)) {
				    					TirarVip2(p,n.trim());
				    					temp.setTime(vip2.getTime());
				    				}
				    				else {
				    					fim = n.trim();
				    					getConfig().set("vips."+getRealName(p.getName())+".inicio", fmt.format(temp.getTime()));
				    					saveConfig();
				    					break;
				    				}
				    			}
							}
						TirarVip(p,usando.trim(),fim);
					}
					else {
						int total=0;
						while(!fmt.format(now.getTime()).equals(fmt.format(vip_fixo.getTime()))) {
							vip_fixo.add(Calendar.DATE, 1);
							total++;
						}
						getConfig().set("vips."+getRealName(p.getName())+"."+usando, (dias-total));
						getConfig().set("vips."+getRealName(p.getName())+".inicio", fmt.format(now.getTime()));
						saveConfig();
					}
				}
	    	}
	    	else {
	    		if(getConfig().getBoolean("rvip_unlisted")) {
		    		for(String n : getConfig().getStringList("vip_groups")) {
		    			if(getServer().getPluginManager().getPlugin("GroupManager")!=null&&!use_vault_for_perms) {
		    				List<String> l = hook.getGroups(p);
		    				if(l.contains(n.trim()))
		    					hook.setGroup(p, getConfig().getString("default_group").trim());
		    	    	}
		    	    	else if(getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!use_vault_for_perms) {
		    	    		PermissionUser user  = PermissionsEx.getUser(p);
		    	    		String[] l = user.getGroupsNames();
		    	    		String[] d = {getConfig().getString("default_group").trim()};
		    	    		for(int i=0;i<l.length;i++)
		    	    			if(l[i].equalsIgnoreCase(n.trim()))
		    	    				user.setGroups(d);
		    	    	}
		    	    	else if(getServer().getPluginManager().getPlugin("FullPVP")!=null&&!use_vault_for_perms) {
		    	    		Permissoes user = new Permissoes(p);
		    	    		if(user.hasPermission("fullpvp.vip"))
		    	    			user.removePermission("fullpvp.vip");
		    	    	}
		    	    	else {
		    	    		if(perms.playerInGroup(p, n.trim())) {
		    	    			perms.playerRemoveGroup(p, n.trim());
		    	    			perms.playerAddGroup(p, getConfig().getString("default_group").trim());
		    	    		}
		    	    	}
		    		}
		    	}
	    	}
	   	}
	   	else {
			ThreadVZ t = new ThreadVZ(this,"atualizar",p);
			t.start();
	   	}
	}
	
	protected String getRealName(String name) {
		if(name==null)
			return null;
		if(getConfig().getBoolean("case_sensitive_for_flatfile"))
			return name;
		if(!getConfig().contains("vips"))
			return name;
		for(String s : getConfig().getConfigurationSection("vips").getKeys(false))
			if(s.toLowerCase().equals(name.toLowerCase()))
				return s;
		return name;
	}
	
	private String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	protected String FormatKey() {
		String key = "";
		int t = 0;
		Random n = new Random();
		int tmax = getConfig().getInt("key_length");
		if(tmax<1||tmax>10)
			tmax=10;
		while(t<tmax) {
			switch(n.nextInt(2)) {
				case 0: {key+=letras[n.nextInt(letras.length)];break;}
				case 1: {key+=String.valueOf(n.nextInt(10));break;}
			}
			t++;
		}
		return key;
	}
}
