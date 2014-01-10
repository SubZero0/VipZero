package me.subzero0.vipzero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Items extends Thread {
	private String group;
	private Main plugin;
	private int dias;
	private Player p;
	public Items(Main main,Player p2,int days,String grupo) {
		plugin=main;
		dias=days;
		group=grupo;
		p=p2;
	}
	
	public void run() {
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		List<String> list = plugin.getConfig().getStringList("vip_items");
    	for(String i : list) {
    		String group2 = i.split(",")[0];
    		boolean achou = false;
    		if(group2.contains("-")) {
    			String[] gps = group2.trim().split("-");
    			for(int w=0;w<gps.length;w++)
    				if(gps[w].trim().equalsIgnoreCase(group)) {
    					achou=true;
    					break;
    				}
    		}
    		else
    			if(group2.trim().equalsIgnoreCase(group))
    				achou=true;
    		if(achou) {
	    		int dias_min = Integer.parseInt(i.split(",")[1]);
	    		if(dias_min<=dias) {
	    			String text;
	    			try {
						text = i.split(",")[2].split(":")[0];
					}
					catch(Exception e) {
						text = i.split(",")[2];
					}
	    			if(!text.equalsIgnoreCase("potion")&&!text.equalsIgnoreCase("$")&&!text.equalsIgnoreCase("xp")&&!text.equalsIgnoreCase("command")&&!text.equalsIgnoreCase("comando")&&!text.equalsIgnoreCase("cmd")&&!text.equalsIgnoreCase("mensagem")&&!text.equalsIgnoreCase("message")&&!text.equalsIgnoreCase("msg")&&!text.equalsIgnoreCase("mcmmo")) {
	    				int item_id = 0;
	    				int item_special = 0;
	    				if(i.split(",")[2].contains("-")) {
	    					String[] ids = i.split(",")[2].split("-");
	    					Random rnd = new Random();
	    					int id = rnd.nextInt(ids.length);
	    					try {
		    					item_id = Integer.parseInt(i.split(",")[2].split("-")[id].split(":")[0]);
		    					item_special = Integer.parseInt(i.split(",")[2].split("-")[id].split(":")[1]);
		    				}
		    				catch(Exception e) {
		    					item_id = Integer.parseInt(i.split(",")[2].split("-")[id]);
		    				}
	    				}
	    				else {
		    				try {
		    					item_id = Integer.parseInt(i.split(",")[2].split(":")[0]);
		    					item_special = Integer.parseInt(i.split(",")[2].split(":")[1]);
		    				}
		    				catch(Exception e) {
		    					item_id = Integer.parseInt(i.split(",")[2]);
		    				}
	    				}
		    			int item_quant = Integer.parseInt(i.split(",")[3]);
			    		ItemStack is = new ItemStack(item_id,item_quant,(short)item_special);
			    		String enc = i.split(",")[4];
			    		if(enc.equalsIgnoreCase("none")) {
			    			HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
		                    leftOver.putAll(p.getInventory().addItem(is));
		                    final ItemStack is2 = is;
		                    if (!leftOver.isEmpty())
		                    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		                    		public void run() {
		                    			p.getWorld().dropItem(p.getLocation(), is2);
		                    		}
		                    	});
			    		}
			    		else {
			    			String[] enchants = i.split(",")[4].split("-");
			    			for(int y=1;y<enchants.length;y+=2) {
			    				String name = "";
			    				List<String> ids = new ArrayList<String>();
			    				if(enchants[y-1].contains("(")&&enchants[y-1].contains(")")) {
			    					String id = enchants[y-1].substring(enchants[y-1].indexOf("("),enchants[y-1].indexOf(")")+1);
			    					name = enchants[y-1].replace(id, "");
			    					id = id.substring(1, id.length()-1);
			    					if(id.contains(";")) {
			    						for(String n : id.split(";")) {
			    							if(n.contains(":"))
				    							ids.add(n);
				    						else
				    							ids.add(n+":0");
			    						}
			    					}
			    					else {
			    						if(id.contains(":"))
			    							ids.add(id);
			    						else
			    							ids.add(id+":0");
			    					}
			    				}
			    				else
			    					name = enchants[y-1];
			    				String opt = enchants[y];
			    				if(ids.size()==0||ids.contains(item_id+":"+item_special)) {
				    				try {
					    				switch(name.toLowerCase()) {
					    					case "power": {is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, Integer.parseInt(opt));break;}
					    					case "flame": {is.addUnsafeEnchantment(Enchantment.ARROW_FIRE, Integer.parseInt(opt));break;}
					    					case "infinity": {is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, Integer.parseInt(opt));break;}
					    					case "punch": {is.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, Integer.parseInt(opt));break;}
					    					case "sharpness": {is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Integer.parseInt(opt));break;}
					    					case "baneofarthropods": {is.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, Integer.parseInt(opt));break;}
					    					case "smite": {is.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, Integer.parseInt(opt));break;}
					    					case "efficiency": {is.addUnsafeEnchantment(Enchantment.DIG_SPEED, Integer.parseInt(opt));break;}
					    					case "unbreaking": {is.addUnsafeEnchantment(Enchantment.DURABILITY, Integer.parseInt(opt));break;}
					    					case "fireaspect": {is.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, Integer.parseInt(opt));break;}
					    					case "knockback": {is.addUnsafeEnchantment(Enchantment.KNOCKBACK, Integer.parseInt(opt));break;}
					    					case "fortune": {is.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, Integer.parseInt(opt));break;}
					    					case "looting": {is.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, Integer.parseInt(opt));break;}
					    					case "respiration": {is.addUnsafeEnchantment(Enchantment.OXYGEN, Integer.parseInt(opt));break;}
					    					case "protection": {is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.parseInt(opt));break;}
					    					case "blastprotection": {is.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, Integer.parseInt(opt));break;}
					    					case "featherfalling": {is.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, Integer.parseInt(opt));break;}
					    					case "fireprotection": {is.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, Integer.parseInt(opt));break;}
					    					case "projectileprotection": {is.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, Integer.parseInt(opt));break;}
					    					case "silktouch": {is.addUnsafeEnchantment(Enchantment.SILK_TOUCH, Integer.parseInt(opt));break;}
					    					case "thorns": {is.addUnsafeEnchantment(Enchantment.THORNS, Integer.parseInt(opt));break;}
					    					case "aquaaffinity": {is.addUnsafeEnchantment(Enchantment.WATER_WORKER, Integer.parseInt(opt));break;}
					    					case "name": {
					    						ItemMeta im = is.getItemMeta();
					    						opt = opt.replaceAll("&", "§");
					    						opt = opt.replaceAll("@player", p.getName());
					    						im.setDisplayName(opt);
					    						is.setItemMeta(im);
					    						break;
					    					}
					    					case "desc": {
					    						ItemMeta im = is.getItemMeta();
					    						ArrayList<String> lore = new ArrayList<String>();
					    						opt = opt.replaceAll("&", "§");
					    						opt = opt.replaceAll("@player", p.getName());
					    						if(im.getLore()!=null)
						    						for(String n : im.getLore())
						    							lore.add(n);
					    						lore.add(opt);
					    						im.setLore(lore);
					    						is.setItemMeta(im);
					    						break;
					    					}
					    					default: {break;}
					    				}
				    				}
				    				catch(Exception e) {
				    					plugin.getLogger().warning("ERROR - Info: "+name+" / Info2: "+opt);
				    					e.printStackTrace();
				    				}
			    				}
			    			}
			    			HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
		                    leftOver.putAll(p.getInventory().addItem(is));
		                    final ItemStack is2 = is;
		                    if (!leftOver.isEmpty())
		                    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		                    		public void run() {
		                    			p.getWorld().dropItem(p.getLocation(), is2);
		                    		}
		                    	});
			    		}
		    		}
		    		else if(text.equalsIgnoreCase("potion")) {
		    			try {
							String tipo = i.split(",")[2].split(":")[1];
							boolean splash = Boolean.parseBoolean(i.split(",")[2].split(":")[2]);
							int tempo = Integer.parseInt(i.split(",")[2].split(":")[3]);
							int pior = Integer.parseInt(i.split(",")[2].split(":")[4]);
							int item_quant = Integer.parseInt(i.split(",")[3]);
							String enc = i.split(",")[4];
							Potion pot = null;
							switch(tipo) {
								case "fireresistance": {pot = new Potion(PotionType.FIRE_RESISTANCE);break;}
								case "instantdamage": {pot = new Potion(PotionType.INSTANT_DAMAGE);break;}
								case "instantheal": {pot = new Potion(PotionType.INSTANT_HEAL);break;}
								case "invisibility": {pot = new Potion(PotionType.INVISIBILITY);break;}
								case "nightvision": {pot = new Potion(PotionType.NIGHT_VISION);break;}
								case "poison": {pot = new Potion(PotionType.POISON);break;}
								case "regen": {pot = new Potion(PotionType.REGEN);break;}
								case "slowness": {pot = new Potion(PotionType.SLOWNESS);break;}
								case "speed": {pot = new Potion(PotionType.SPEED);break;}
								case "strength": {pot = new Potion(PotionType.STRENGTH);break;}
								case "water": {pot = new Potion(PotionType.WATER);break;}
								case "weakness": {pot = new Potion(PotionType.WEAKNESS);break;}
								default: {break;}
							}
							if(splash)
			    				pot.splash();
							ItemStack sample = new ItemStack(pot.toItemStack(item_quant));
			    			PotionMeta pm = (PotionMeta) sample.getItemMeta();
			    			switch(tipo.toLowerCase()) {
			    				case "fireresistance": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "instantdamage": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.HARM, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "instantheal": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "invisibility": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "nightvision": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "poison": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.POISON, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "regen": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "slowness": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "speed": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "strength": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "water": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
								case "weakness": {
			    					pm.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, tempo*20, pior), true);
			    					sample.setItemMeta(pm);
			    					break;
			    				}
		    					default: {break;}
		    				}
				    		if(!enc.equalsIgnoreCase("none")) {
				    			String[] enchants = i.split(",")[4].split("-");
				    			for(int y=1;y<enchants.length;y+=2) {
				    				String name = enchants[y-1];
				    				String opt = enchants[y];
				    				try {
					    				switch(name.toLowerCase()) {
						    				case "fireresistance": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "instantdamage": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.HARM, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "instantheal": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "invisibility": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "nightvision": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "poison": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.POISON, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "regen": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "slowness": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "speed": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "strength": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "water": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "weakness": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "blindness": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "confusion": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "hunger": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					sample.setItemMeta(pm);
						    					break;
						    				}
											case "jump": {
						    					pm.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, Integer.parseInt(opt.split(":")[0])*20, Integer.parseInt(opt.split(":")[1])), true);
						    					break;
						    				}
											case "name": {
												opt = opt.replaceAll("&", "§");
												opt = opt.replaceAll("@player", p.getName());
					    						pm.setDisplayName(opt);
					    						sample.setItemMeta(pm);
					    						break;
					    					}
					    					case "desc": {
					    						ArrayList<String> lore = new ArrayList<String>();
					    						opt = opt.replaceAll("&", "§");
					    						opt = opt.replaceAll("@player", p.getName());
					    						if(pm.getLore()!=null)
						    						for(String n : pm.getLore())
						    							lore.add(n);
					    						lore.add(opt);
					    						pm.setLore(lore);
					    						sample.setItemMeta(pm);
					    						break;
					    					}
					    					default: {break;}
					    				}
				    				}
				    				catch(Exception e) {
				    					plugin.getLogger().warning("ERROR - Info: "+name+" / Info2: "+opt);
				    					e.printStackTrace();
				    				}
				    			}
				    		}
				    		HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
		                    leftOver.putAll(p.getInventory().addItem(sample));
		                    final ItemStack sample2 = sample;
		                    if (!leftOver.isEmpty())
		                    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		                    		public void run() {
		                    			p.getWorld().dropItem(p.getLocation(), sample2);
		                    		}
		                    	});
						}
						catch(Exception e) {
							plugin.getLogger().warning("ERROR - Info: "+i.split(",")[2]);
							e.printStackTrace();
						}
		    		}
		    		else if(text.equalsIgnoreCase("$")) {
		    			if(!plugin.block_dinheiro) {
		    				int item_quant = Integer.parseInt(i.split(",")[3]);
		    				EconomyResponse r = Main.econ.depositPlayer(p.getName(), item_quant);
				            if(!r.transactionSuccess())
				                plugin.getLogger().info(String.format("ERROR: %s", r.errorMessage));
		    			}
		    			else
		    				plugin.getLogger().info("ERROR - BlockMoney");
		    		}
		    		else if(text.equalsIgnoreCase("xp")) {
		    			int xp_quant = Integer.parseInt(i.split(",")[3]);
		    			p.giveExpLevels(xp_quant);
		    		}
		    		else if(text.equalsIgnoreCase("command")||text.equalsIgnoreCase("comando")||text.equalsIgnoreCase("cmd")) {
		    			String command = i.split(",")[3].replaceAll("@player", p.getName());
		    			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),command);
		    		}
		    		else if(text.equalsIgnoreCase("message")||text.equalsIgnoreCase("mensagem")||text.equalsIgnoreCase("msg")) {
		    			String msg = i.split(",")[3].replaceAll("@player", p.getName());
		    			msg = msg.replaceAll("&","§");
		    			p.sendMessage(msg);
		    		}
		    		else {
		    			plugin.getLogger().info("ERROR - No type - Info: "+text);
		    		}
	    		}
	    	}
    	}
	}
}
