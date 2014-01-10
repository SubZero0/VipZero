package me.subzero0.vipzero;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Commands implements CommandExecutor {
	private Main plugin;
	public Commands(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("addvip")) {
			if(sender.hasPermission("vipzero.addvip")||sender.hasPermission("vipzero.admin")||sender.isOp()) {
				if(args.length==2) {
					try {
						String grupo = args[0];
						boolean achou_grupo = false;
						for(String gs : plugin.getConfig().getStringList("vip_groups"))
							if(gs.trim().equalsIgnoreCase(grupo)) {
								achou_grupo=true;
								grupo = gs.trim();
								break;
							}
						if(achou_grupo) {
							int dias = Integer.parseInt(args[1]);
							if(dias>0) {
								if(plugin.flatfile) {
									for(String n : plugin.getConfig().getConfigurationSection("vips").getKeys(false)) {
										if(plugin.getConfig().contains("vips."+n.trim()+"."+grupo)) {
											int old = plugin.getConfig().getInt("vips."+n.trim()+"."+grupo); 
											if(old!=0)
												plugin.getConfig().set("vips."+n.trim()+"."+grupo, old+dias);
										}
									}
									plugin.saveConfig();
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("addvip").trim().replaceAll("%days%", Integer.toString(dias).replaceAll("%group%", grupo))+".");
									plugin.reloadConfig();
								}
								else {
									ThreadVZ nk = new ThreadVZ(plugin,"addvip",sender,grupo,dias);
									nk.start();
								}
							}
							else
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error1")+".");
						}
						else
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+".");
					}
					catch(NumberFormatException e) {
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error2")+".");
					}
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/addvip <"+plugin.getMessage("group")+"> <"+plugin.getMessage("days")+">");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("darvip")||cmd.getName().equalsIgnoreCase("givevip")) {
			if(sender.hasPermission("vipzero.darvip")||sender.hasPermission("vipzero.givevip")||sender.hasPermission("vipzero.admin")||sender.isOp()||sender==plugin.getServer().getConsoleSender()) {
				if(args.length==3) {
					Player p = plugin.getServer().getPlayer(args[0]);
					if(p!=null) {
						boolean achou = false;
						String grupo = "";
						for(String gName : plugin.getConfig().getStringList("vip_groups"))
							if(gName.trim().equalsIgnoreCase(args[1].trim())) {
								achou=true;
								grupo=gName.trim();
								break;
							}
						if(achou) {
							try {
								int dias = Integer.parseInt(args[2].trim());
								if(dias>0&&dias<100000) {
									if(plugin.flatfile) {
										if(plugin.usekey_global)
											plugin.getServer().broadcastMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success3").trim().replaceAll("%name%", p.getName()).replaceAll("%group%", grupo).replaceAll("%days%", Integer.toString(dias))+"!");
										else
											p.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success2").replaceAll("%group%", grupo.toUpperCase()).replaceAll("%days%", Integer.toString(dias))+"!");
										if(!plugin.getConfig().contains("vips."+plugin.getRealName(p.getName()))) {
											Calendar now = Calendar.getInstance();
											SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
											plugin.getConfig().set("vips."+p.getName()+".inicio", fmt.format(now.getTime()));
											plugin.getConfig().set("vips."+p.getName()+".usando", grupo);
											plugin.getConfig().set("vips."+p.getName()+"."+grupo, dias);
											plugin.saveConfig();
											plugin.DarVip(p, dias, grupo.trim());
										}
										else {
											if(plugin.getConfig().contains("vips."+plugin.getRealName(p.getName())+"."+grupo))
												plugin.getConfig().set("vips."+plugin.getRealName(p.getName())+"."+grupo, (plugin.getConfig().getInt("vips."+plugin.getRealName(p.getName())+"."+grupo)+dias));
											else
												plugin.getConfig().set("vips."+plugin.getRealName(p.getName())+"."+grupo, dias);
											plugin.saveConfig();
											plugin.DarItensVip(p, dias, grupo.trim());
										}
									}
									else {
										ThreadVZ t = new ThreadVZ(plugin,"givevip",p,dias,grupo);
										t.start();
									}
								}
								else {
									if(sender!=plugin.getServer().getConsoleSender())
										sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error1")+"!");
									else
										plugin.getLogger().info(plugin.getMessage("error1")+"!");
								}
							}
							catch(Exception e) {
								if(sender!=plugin.getServer().getConsoleSender())
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error2")+"!");
								else
									plugin.getLogger().info(plugin.getMessage("error2")+"!");
							}
						}
						else {
							if(sender!=plugin.getServer().getConsoleSender())
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+"!");
							else
								plugin.getLogger().info(plugin.getMessage("error8")+"!");
						}
					}
					else {
						if(sender!=plugin.getServer().getConsoleSender())
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error7")+"!");
						else
							plugin.getLogger().info(plugin.getMessage("error7")+"!");
					}
				}
				else {
					if(sender!=plugin.getServer().getConsoleSender())
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.RED+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "darvip" : "givevip")+" <"+plugin.getMessage("name")+"> <"+plugin.getMessage("group")+"> <"+plugin.getMessage("days")+">");
					else
						plugin.getLogger().info((plugin.getLanguage().trim().equalsIgnoreCase("br") ? "darvip" : "givevip")+" <"+plugin.getMessage("name")+"> <"+plugin.getMessage("group")+"> <"+plugin.getMessage("days")+">");
				}
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("mudardias")||cmd.getName().equalsIgnoreCase("changedays")) {
			if(sender.hasPermission("vipzero.mudardias")||sender.hasPermission("vipzero.changedays")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length>0&&args.length<4) {
					Player p = plugin.getServer().getPlayer(args[0]);
					if(p!=null) {
						if(args.length<3) {
							if(plugin.flatfile) {
								if(plugin.getConfig().contains("vips."+plugin.getRealName(p.getName()))) {
									sender.sendMessage(ChatColor.DARK_AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+p.getName()+" - "+plugin.getMessage("message2")+":");
									sender.sendMessage(ChatColor.AQUA+plugin.getMessage("initialdate")+": "+ChatColor.WHITE+plugin.getConfig().getString("vips."+plugin.getRealName(p.getName())+".inicio"));
									for(String gname : plugin.getConfig().getStringList("vip_groups"))
										if(plugin.getConfig().contains("vips."+plugin.getRealName(p.getName())+"."+gname.trim()))
											sender.sendMessage(ChatColor.AQUA+gname.toUpperCase()+ChatColor.WHITE+" - "+plugin.getMessage("daysleft")+": "+plugin.getConfig().getInt("vips."+plugin.getRealName(p.getName())+"."+gname)+" "+plugin.getMessage("days"));
								}
								else
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+p.getName()+" "+plugin.getMessage("error9")+"!");
							}
							else {
								ThreadVZ t = new ThreadVZ(plugin,"mudardias1",sender,p);
								t.start();
							}
						}
						else {
							if(plugin.flatfile) {
								if(plugin.getConfig().contains("vips."+plugin.getRealName(p.getName()))) {
									boolean achou = false;
									String grupo = "";
									for(String gName : plugin.getConfig().getStringList("vip_groups"))
										if(gName.trim().equalsIgnoreCase(args[1].trim())) {
											achou=true;
											grupo=gName.trim();
											break;
										}
									if(achou) {
										try {
											int dias = Integer.parseInt(args[2].trim());
											if(dias>1&&dias<100000) {
												plugin.getConfig().set("vips."+plugin.getRealName(p.getName())+"."+grupo,dias);
												plugin.saveConfig();
												plugin.getServer().broadcastMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("cdays").trim().replaceAll("%admin%", sender.getName()).replaceAll("%group%", grupo).replaceAll("%name%", p.getName()).replaceAll("%days%", Integer.toString(dias))+"!");
											}
											else
												sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error1")+"!");
										}
										catch(Exception e) {sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+"!");}
									}
									else
										sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error2")+"!");
								}
								else
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+p.getName()+" "+plugin.getMessage("error9")+"!");
							}
							else {
								try {
									boolean achou = false;
									String grupo = "";
									for(String gName : plugin.getConfig().getStringList("vip_groups"))
										if(gName.trim().equalsIgnoreCase(args[1].trim())) {
											achou=true;
											grupo=gName.trim();
											break;
										}
									if(achou) {
										ThreadVZ t = new ThreadVZ(plugin,"mudardias2",p,args,sender,grupo);
										t.start();
									}
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					else
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error7")+"!");
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "mudardias" : "changedays")+" <"+plugin.getMessage("name")+"> <"+plugin.getMessage("group")+"> <"+plugin.getMessage("days")+">");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("trocarvip")||cmd.getName().equalsIgnoreCase("changevip")) {
			if(sender.hasPermission("vipzero.trocarvip")||sender.hasPermission("vipzero.changevip")||sender.isOp()||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length==1) {
					if(plugin.flatfile) {
						if(plugin.getConfig().contains("vips."+sender.getName())) {
							boolean achou = false;
							String grupo = "";
							for(String gName : plugin.getConfig().getStringList("vip_groups"))
								if(gName.trim().equalsIgnoreCase(args[0].trim())) {
									achou=true;
									grupo=gName.trim();
									break;
								}
							if(achou) {
								Calendar now = Calendar.getInstance();
								SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
								boolean blockuso = false;
								if(plugin.getConfig().getBoolean("one_vip_change"))
									if(plugin.trocou.containsKey(sender.getName()))
										if(fmt.format(now.getTime()).equals(plugin.trocou.get(sender.getName())))
											blockuso=true;
								if(!blockuso) {
									if(plugin.getConfig().contains("vips."+sender.getName()+"."+grupo)) {
										if(plugin.getConfig().getInt("vips."+sender.getName()+"."+grupo)>0) {
											if(plugin.getServer().getPluginManager().getPlugin("GroupManager")!=null&&!plugin.use_vault_for_perms) {
												plugin.hook.setGroup((Player)sender, grupo);
											}
											else if(plugin.getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!plugin.use_vault_for_perms) {
												PermissionUser user  = PermissionsEx.getUser((Player)sender);
												String[] n = {grupo};
												user.setGroups(n);
											}
											else {
												for(String g : plugin.getConfig().getStringList("vip_groups"))
													if(Main.perms.playerInGroup((Player)sender, g.trim()))
														Main.perms.playerRemoveGroup((Player)sender, g.trim());
												Main.perms.playerAddGroup((Player)sender, grupo);
									    	}
											if(plugin.getConfig().getBoolean("one_vip_change")) {
												if(plugin.trocou.containsKey(sender.getName()))
													plugin.trocou.remove(sender.getName());
												plugin.trocou.put(sender.getName(), fmt.format(now.getTime()));
											}
											plugin.getConfig().set("vips."+sender.getName()+".usando", grupo);
											plugin.saveConfig();
											sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success4")+"!");
										}
										else
											sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error12")+"!");
									}
									else
										sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error12")+"!");
								}
								else
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error10")+"!");
							}
							else
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+"!");
						}
						else
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error6")+"!");
					}
					else {
						boolean achou = false;
						String grupo = "";
						for(String gName : plugin.getConfig().getStringList("vip_groups"))
							if(gName.trim().equalsIgnoreCase(args[0].trim())) {
								achou=true;
								grupo=gName.trim();
								break;
							}
						if(achou) {
							ThreadVZ t = new ThreadVZ(plugin,"trocarvip",sender,grupo);
							t.start();
						}
						else
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+"!");
					}
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "trocarvip" : "changevip")+" <"+plugin.getMessage("group")+">");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("gerarkey")||cmd.getName().equalsIgnoreCase("newkey")) {
			if(sender.hasPermission("vipzero.gerarkey")||sender.hasPermission("vipzero.newkey")||sender.hasPermission("vipzero.admin")||sender.isOp()) {
				if(args.length==2) {
					try {
						String grupo = args[0];
						boolean achou_grupo = false;
						for(String gs : plugin.getConfig().getStringList("vip_groups"))
							if(gs.trim().equalsIgnoreCase(grupo)) {
								achou_grupo=true;
								grupo = gs.trim();
								break;
							}
						if(achou_grupo) {
							int dias = Integer.parseInt(args[1]);
							if(dias>0) {
								String key = plugin.FormatKey();
								if(plugin.flatfile) {
									while(plugin.getConfig().contains("keys."+key)) {
										key = plugin.FormatKey();
									}
									plugin.getConfig().set("keys."+key, grupo+","+Integer.toString(dias));
									plugin.saveConfig();
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"Key: "+ChatColor.GREEN+key+ChatColor.WHITE+" ("+grupo.toUpperCase()+") - "+ChatColor.GREEN+dias+ChatColor.WHITE+" "+plugin.getMessage("message1")+".");
									plugin.reloadConfig();
								}
								else {
									ThreadVZ nk = new ThreadVZ(plugin,"newkey",sender,grupo,dias,key);
									nk.start();
								}
							}
							else
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error1")+".");
						}
						else
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error8")+".");
					}
					catch(NumberFormatException e) {
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error2")+".");
					}
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "gerarkey" : "newkey")+" <"+plugin.getMessage("group")+"> <"+plugin.getMessage("days")+">");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("keys")) {
			if(sender.hasPermission("vipzero.keys")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(plugin.flatfile) {
					if(plugin.getConfig().contains("keys")) {
						Set<String> keys = plugin.getConfig().getConfigurationSection("keys").getKeys(false);
						if(keys.size()==0) 
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error3")+".");
						else {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+plugin.getMessage("list")+":");
							for(String n : keys) {
								sender.sendMessage(ChatColor.WHITE+"Key: "+ChatColor.GREEN+n+ChatColor.WHITE+" ("+plugin.getConfig().getString("keys."+n).split(",")[0].toUpperCase()+") - "+WordUtils.capitalizeFully(plugin.getMessage("days"))+": "+ChatColor.GREEN+plugin.getConfig().getString("keys."+n).split(",")[1]);
							}
						}
					}
					else
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error4")+".");
				}
				else {
					ThreadVZ t = new ThreadVZ(plugin,"keys",sender);
					t.start();
				}
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("apagarkeys")||cmd.getName().equalsIgnoreCase("delkeys")) {
			if(sender.hasPermission("vipzero.apagarkeys")||sender.hasPermission("vipzero.delkeys")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(plugin.flatfile) {
					plugin.getConfig().set("keys", null);
					plugin.saveConfig();
					plugin.reloadConfig();
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success1")+"!");
				}
				else {
					ThreadVZ t = new ThreadVZ(plugin,"delkeys",sender);
					t.start();
				}
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("apagarkey")||cmd.getName().equalsIgnoreCase("delkey")) {
			if(sender.hasPermission("vipzero.apagarkey")||sender.hasPermission("vipzero.delkey")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length==0) {
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "apagarkey" : "delkey")+" <key>");
					return true;
				}
				String key = args[0].toUpperCase();
				if(plugin.flatfile) {
					if(!plugin.getConfig().contains("keys."+key)) {
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error5")+"!");
						return true;
					}
					plugin.getConfig().set("keys."+key, null);
					plugin.saveConfig();
					plugin.reloadConfig();
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success5").replaceAll("%key%", key)+"!");
				}
				else {
					ThreadVZ t = new ThreadVZ(plugin,"delkey",key,sender);
					t.start();
				}
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("usarkey")||cmd.getName().equalsIgnoreCase("usekey")) {
			if(sender.hasPermission("vipzero.usarkey")||sender.hasPermission("vipzero.usekey")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length==1) {
					String key = args[0].toUpperCase();
					if(!plugin.using_codes.containsKey(key)) {
						plugin.using_codes.put(key,"");
						if(plugin.flatfile) {
							if(plugin.getConfig().contains("keys."+key)) {
								String grupo = plugin.getConfig().getString("keys."+key).split(",")[0].trim();
								int dias = Integer.parseInt(plugin.getConfig().getString("keys."+key).split(",")[1].trim());
								if(plugin.usekey_global)
									plugin.getServer().broadcastMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success3").trim().replaceAll("%name%", sender.getName()).replaceAll("%group%", grupo).replaceAll("%days%", Integer.toString(dias))+"!");
								else
									sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("success2").replaceAll("%group%", grupo).replaceAll("%days%", Integer.toString(dias))+"!");
								if(!plugin.getConfig().contains("vips."+sender.getName())) {
									Calendar now = Calendar.getInstance();
									SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
									plugin.getConfig().set("vips."+sender.getName()+".inicio", fmt.format(now.getTime()));
									plugin.getConfig().set("vips."+sender.getName()+".usando", grupo);
									plugin.getConfig().set("vips."+sender.getName()+"."+grupo, dias);
									plugin.saveConfig();
									plugin.DarVip(((Player)sender), dias, grupo.trim());
								}
								else {
									if(plugin.getConfig().contains("vips."+sender.getName()+"."+grupo))
										plugin.getConfig().set("vips."+sender.getName()+"."+grupo, (plugin.getConfig().getInt("vips."+sender.getName()+"."+grupo)+dias));
									else
										plugin.getConfig().set("vips."+sender.getName()+"."+grupo, dias);
									plugin.saveConfig();
									plugin.DarItensVip(((Player)sender), dias, grupo);
								}
								plugin.getConfig().set("keys."+key, null);
								plugin.saveConfig();
								if(plugin.getConfig().getBoolean("logging.usekey")) {
									try {
										Calendar now = Calendar.getInstance();
										SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
									    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(plugin.getDataFolder()+File.separator+"log.txt", true)));
									    out.println("usekey|"+sender.getName()+"|"+key+"|"+fmt.format(now.getTime())+"|"+grupo+"|"+dias);
									    out.close();
									} catch (Exception e) {}
								}
							}
							else
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error5")+"!");
							plugin.using_codes.remove(key);
						}
						else {
							ThreadVZ t = new ThreadVZ(plugin,"usekey",key,sender);
							t.start();
						}
					}
					else
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error13")+"!");	
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "usarkey" : "usekey")+" <key>");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("tempovip")||cmd.getName().equalsIgnoreCase("viptime")) {
			if(sender.hasPermission("vipzero.tempovip")||sender.hasPermission("vipzero.viptime")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(plugin.flatfile) {
					if(plugin.getConfig().contains("vips."+sender.getName())) {
						sender.sendMessage(ChatColor.DARK_AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+plugin.getMessage("message2")+":");
						sender.sendMessage(ChatColor.AQUA+plugin.getMessage("initialdate")+": "+ChatColor.WHITE+plugin.getConfig().getString("vips."+sender.getName()+".inicio"));
						for(String gname : plugin.getConfig().getStringList("vip_groups"))
							if(plugin.getConfig().contains("vips."+sender.getName()+"."+gname.trim()))
								sender.sendMessage(ChatColor.AQUA+gname.toUpperCase()+ChatColor.WHITE+" - "+plugin.getMessage("daysleft")+": "+plugin.getConfig().getInt("vips."+sender.getName()+"."+gname)+" "+plugin.getMessage("days"));
					}
					else
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error6")+"!");
				}
				else {
					ThreadVZ t = new ThreadVZ(plugin,"tempovip",sender);
					t.start();
				}
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("vipzero")) {
			if(sender.hasPermission("vipzero.reload")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length==1)
					if(args[0].equalsIgnoreCase("reload")) {
						plugin.reloadConfig();
						File lFile = new File(plugin.getDataFolder(), "language_"+plugin.getConfig().getString("language").trim()+".yml");
						plugin.language = YamlConfiguration.loadConfiguration(lFile);
						File lFile2 = new File(plugin.getDataFolder(), "pagseguro.yml");
						plugin.pagseguro = YamlConfiguration.loadConfiguration(lFile2);
						File lFile3 = new File(plugin.getDataFolder(), "paypal.yml");
						plugin.paypal = YamlConfiguration.loadConfiguration(lFile3);
						for(Player p : plugin.getServer().getOnlinePlayers())
							plugin.AtualizarVIP(p);
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("reload")+"!");
						return true;
					}
			}
			if(sender.hasPermission("vipzero.pagseguro")||sender.isOp()||sender.hasPermission("vipzero.admin")||sender.isOp()||sender.hasPermission("vipzero.user")) {
				if(args.length>=1)
					if(args[0].equalsIgnoreCase("pagseguro")) {
						if(!plugin.use_pagseguro) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"PagSeguro inactive. =(");
							return true;
						}
						if(args.length==1) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/vipzero pagseguro <transaction-code>");
							return true;
						}
						if(plugin.pagseguro.contains(args[1].toUpperCase())) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error14")+"!");
							return true;
						}
						if(plugin.using_ps.containsKey(args[1].toUpperCase())) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error13")+"!");
							return true;
						}
						plugin.using_ps.put(args[1].toUpperCase(), "");
						PagSeguro ps = new PagSeguro(plugin,args[1],sender);
						ps.start();
						return true;
					}
			}
			if(sender.hasPermission("vipzero.paypal")||sender.isOp()||sender.hasPermission("vipzero.admin")||sender.isOp()||sender.hasPermission("vipzero.user")) {
				if(args.length>=1)
					if(args[0].equalsIgnoreCase("paypal")) {
						if(!plugin.use_paypal) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"PayPal inactive. =(");
							return true;
						}
						if(args.length==1) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/vipzero paypal <transaction-code>");
							return true;
						}
						if(plugin.paypal.contains(args[1].toUpperCase())) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error14")+"!");
							return true;
						}
						if(plugin.using_pp.containsKey(args[1].toUpperCase())) {
							sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error13")+"!");
							return true;
						}
						plugin.using_pp.put(args[1].toUpperCase(), "");
						PayPal pp = new PayPal(plugin,args[1],sender);
						pp.start();
						return true;
					}
			}
			if(plugin.getLanguage().trim().equalsIgnoreCase("br")) {
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.DARK_AQUA+"Comandos do VipZero:");
				if(sender.hasPermission("vipzero.usarkey")||sender.hasPermission("vipzero.usekey")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/usarkey "+ChatColor.WHITE+"- Utiliza uma key VIP.");
				if(sender.hasPermission("vipzero.tempovip")||sender.hasPermission("vipzero.viptime")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/tempovip "+ChatColor.WHITE+"- Mostra o ultimo dia de seu VIP.");
				if(sender.hasPermission("vipzero.trocarvip")||sender.hasPermission("vipzero.changevip")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/trocarvip "+ChatColor.WHITE+"- Muda o VIP que você está usando.");
				if(sender.hasPermission("vipzero.gerarkey")||sender.hasPermission("vipzero.newkey")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/gerarkey "+ChatColor.WHITE+"- Gera uma key com X dias de VIP.");
				if(sender.hasPermission("vipzero.keys")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/keys "+ChatColor.WHITE+"- Lista as keys disponiveis.");
				if(sender.hasPermission("vipzero.apagarkeys")||sender.hasPermission("vipzero.delkeys")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/apagarkeys "+ChatColor.WHITE+"- Apaga as keys disponiveis.");
				if(sender.hasPermission("vipzero.apagarkey")||sender.hasPermission("vipzero.delkey")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/apagarkey "+ChatColor.WHITE+"- Apaga apenas uma key.");
				if(sender.hasPermission("vipzero.tirarvip")||sender.hasPermission("vipzero.rvip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/tirarvip "+ChatColor.WHITE+"- Tira o VIP de um jogador.");
				if(sender.hasPermission("vipzero.mudardias")||sender.hasPermission("vipzero.changedays")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/mudardias "+ChatColor.WHITE+"- Muda os dias de do grupo VIP.");
				if(sender.hasPermission("vipzero.reload")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero reload "+ChatColor.WHITE+"- Recarrega o arquivo de configuração.");
				if(sender.hasPermission("vipzero.pagseguro")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero pagseguro "+ChatColor.WHITE+"- Dá VIP usando o código do PagSeguro.");
				if(sender.hasPermission("vipzero.paypal")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero paypal "+ChatColor.WHITE+"- Dá VIP usando o código do PayPal.");
				if(sender.hasPermission("vipzero.darvip")||sender.hasPermission("vipzero.givevip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/darvip "+ChatColor.WHITE+"- Dá VIP sem o uso de uma key.");
				if(sender.hasPermission("vipzero.addvip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/addvip "+ChatColor.WHITE+"- Dá dias VIPs a todos desse grupo (sem itens).");
			}
			else {
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.DARK_AQUA+"Commands of VipZero:");
				if(sender.hasPermission("vipzero.usarkey")||sender.hasPermission("vipzero.usekey")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/usekey "+ChatColor.WHITE+"- Uses a key.");
				if(sender.hasPermission("vipzero.tempovip")||sender.hasPermission("vipzero.viptime")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/viptime "+ChatColor.WHITE+"- Show your last day with VIP.");
				if(sender.hasPermission("vipzero.trocarvip")||sender.hasPermission("vipzero.changevip")||sender.hasPermission("vipzero.user")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/changevip "+ChatColor.WHITE+"- Changes the VIP that you are using.");
				if(sender.hasPermission("vipzero.gerarkey")||sender.hasPermission("vipzero.newkey")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/newkey "+ChatColor.WHITE+"- Creates a new key with X days.");
				if(sender.hasPermission("vipzero.keys")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/keys "+ChatColor.WHITE+"- List all keys.");
				if(sender.hasPermission("vipzero.apagarkeys")||sender.hasPermission("vipzero.delkeys")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/delkeys "+ChatColor.WHITE+"- Delete all keys.");
				if(sender.hasPermission("vipzero.apagarkey")||sender.hasPermission("vipzero.delkey")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/delkey "+ChatColor.WHITE+"- Delete one key.");
				if(sender.hasPermission("vipzero.tirarvip")||sender.hasPermission("vipzero.rvip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/rvip "+ChatColor.WHITE+"- Remove a VIP from player.");
				if(sender.hasPermission("vipzero.mudardias")||sender.hasPermission("vipzero.changedays")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/changedays "+ChatColor.WHITE+"- Change the days of a VIP.");
				if(sender.hasPermission("vipzero.reload")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero reload "+ChatColor.WHITE+"- Reload config file.");
				if(sender.hasPermission("vipzero.pagseguro")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero pagseguro "+ChatColor.WHITE+"- Give VIP with PagSeguro's transaction code.");
				if(sender.hasPermission("vipzero.paypal")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/vipzero paypal "+ChatColor.WHITE+"- Give VIP with PayPal's transaction code.");
				if(sender.hasPermission("vipzero.darvip")||sender.hasPermission("vipzero.givevip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/givevip "+ChatColor.WHITE+"- Give VIP without generating a key.");
				if(sender.hasPermission("vipzero.addvip")||sender.isOp()||sender.hasPermission("vipzero.admin"))
					sender.sendMessage(ChatColor.AQUA+"/addvip "+ChatColor.WHITE+"- Give VIP days to all from this group (no items).");
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("tirarvip")||cmd.getName().equalsIgnoreCase("rvip")) {
			if(sender.hasPermission("vipzero.tirarvip")||sender.hasPermission("vipzero.rvip")||sender.isOp()||sender.hasPermission("vipzero.admin")) {
				if(args.length==1) {
					Player p = plugin.getServer().getPlayer(args[0]);
					if(p!=null) {
						if(plugin.flatfile) {
							if(plugin.getConfig().contains("vips."+plugin.getRealName(p.getName()))) {
								plugin.getConfig().set("vips."+plugin.getRealName(p.getName()), null);
								plugin.saveConfig();
								if(plugin.getServer().getPluginManager().getPlugin("GroupManager")!=null&&!plugin.use_vault_for_perms) {
									plugin.removeRelatedVipGroups(p);
									plugin.hook.setGroup(p,plugin.getConfig().getString("default_group").trim());
						    	}
						    	else if(plugin.getServer().getPluginManager().getPlugin("PermissionsEx")!=null&&!plugin.use_vault_for_perms) {
						    		PermissionUser user  = PermissionsEx.getUser(p);
						    		plugin.removeRelatedVipGroups(p);
						    		user.addGroup(plugin.getConfig().getString("default_group").trim());
						    	}
						    	else {
						    		plugin.removeRelatedVipGroups(p);
						    		Main.perms.playerAddGroup(p, plugin.getConfig().getString("default_group").trim());
						    	}
								plugin.getServer().broadcastMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("rvip").trim().replaceAll("%admin%", sender.getName()).replaceAll("%name%", p.getName())+"!");
							}
							else
								sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+p.getName()+" "+plugin.getMessage("error9")+"!");
						}
						else {
							ThreadVZ t = new ThreadVZ(plugin,"rvip",sender,p);
							t.start();
						}
					}
					else
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error7")+"!");
				}
				else
					sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"/"+(plugin.getLanguage().trim().equalsIgnoreCase("br") ? "tirarvip" : "rvip")+" <"+plugin.getMessage("name")+">");
			}
			else
				sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error11")+"!");
			return true;
		}
		return false;
	}
}
