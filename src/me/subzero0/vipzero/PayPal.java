package me.subzero0.vipzero;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import paypalnvp.profile.BaseProfile;
import paypalnvp.profile.Profile;
import paypalnvp.request.GetTransactionDetails;

public class PayPal extends Thread {
	private Main plugin = null;
	private String transactID = "";
	private CommandSender sender = null;
	public PayPal(Main pl,String transactID2,CommandSender cmdss) {
		plugin=pl;
		transactID=transactID2.toUpperCase();
		sender=cmdss;
		
	}
	
	public void run() {
		paypalnvp.core.PayPal pp = null;
		GetTransactionDetails tr = null;
		try {
			Profile user = new BaseProfile.Builder(plugin.getConfig().getString("paypal.username"),plugin.getConfig().getString("paypal.password")).signature(plugin.getConfig().getString("paypal.signature")).build();
			pp = new paypalnvp.core.PayPal(user,paypalnvp.core.PayPal.Environment.LIVE);
			tr = new GetTransactionDetails(transactID);
		}
		catch(Exception e) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error15")+"!");
            plugin.using_pp.remove(transactID);
            e.printStackTrace();
            return;
		}
		if (pp==null||tr==null) {
        	sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error15")+"!");
        	plugin.using_pp.remove(transactID);
        	return;
        }
		
		pp.setResponse(tr);
		
		if(!tr.getNVPResponse().containsKey("PAYMENTSTATUS")) {
        	sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error15")+"!");
        	plugin.using_pp.remove(transactID);
        	return;
        }
		if(!tr.getNVPResponse().get("PAYMENTSTATUS").equals("Completed")) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error16")+"!");
			plugin.using_pp.remove(transactID);
			return;
		}
		
		List<String> itens = new ArrayList<String>();
		for(String key : tr.getNVPResponse().keySet())
			if(key.startsWith("L_NAME")) {
				if(tr.getNVPResponse().get(key).contains("(vz:"))
					for(int i=0;i<Integer.parseInt(tr.getNVPResponse().get("L_QTY"+key.charAt(key.length()-1)));i++)
						itens.add(tr.getNVPResponse().get(key).split("\\(vz:")[1].split("\\)")[0]+","+tr.getNVPResponse().get("L_QTY"+key.charAt(key.length()-1)));
			}
		if(itens.size()==0||itens==null) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error17")+"!");
			plugin.using_pp.remove(transactID);
			return;
		}
		
		boolean pedido_valido = true;
		for(String item2 : itens) {
			boolean achou = false;
			for(String gName : plugin.getConfig().getStringList("vip_groups"))
				if(gName.trim().equalsIgnoreCase(item2.split(",")[0]))
					achou=true;
			if(!achou) {
				pedido_valido=false;
				break;
			}
			int days = Integer.parseInt(item2.split(",")[1]);
			if(days<0||days>10000) {
				pedido_valido=false;
				break;
			}
		}
		if(!pedido_valido) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error18")+"!");
			return;
		}
		
		plugin.paypal.set(transactID+".usedBy", sender.getName());
		Calendar now = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		plugin.paypal.set(transactID+".date", fmt.format(now.getTime()));
		try {
			plugin.paypal.save(new File(plugin.getDataFolder(),"paypal.yml"));
		} catch (IOException e) {e.printStackTrace();}
		if(!plugin.flatfile&&plugin.mysql_paypal) {
			try {
				Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
				PreparedStatement addlog = con.prepareStatement("INSERT INTO `vipzero_paypal` (`key`,`nome`,`data`) VALUES ('"+transactID+"','"+sender.getName()+"','"+fmt.format(now.getTime())+"');");
				addlog.execute();
				addlog.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		plugin.using_pp.remove(transactID);
		
		if(plugin.getConfig().getInt("paypal.mode")==1) {
			//dá direto
			for(String item3 : itens)
				for(int i=0;i<Integer.parseInt(item3.split(",")[2]);i++) {
					String grupo = "";
					for(String gName : plugin.getConfig().getStringList("vip_groups"))
						if(gName.trim().equalsIgnoreCase(item3.split(",")[0])) {
							grupo=gName.trim();
							break;
						}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), (plugin.getLanguage().trim().equalsIgnoreCase("br")?"darvip":"givevip")+" "+sender.getName()+" "+grupo+" "+item3.split(",")[1]);
				}
		}
		else /*if(plugin.getConfig().getInt("pagseguro.mode")==2)*/ {
			//dá keys
			for(String item4 : itens)
				for(int i=0;i<Integer.parseInt(item4.split(",")[2]);i++) {
					String grupo = "";
					for(String gName : plugin.getConfig().getStringList("vip_groups"))
						if(gName.trim().equalsIgnoreCase(item4.split(",")[0])) {
							grupo=gName.trim();
							break;
						}
					String key = plugin.FormatKey();
					if(plugin.flatfile) {
						while(plugin.getConfig().contains("keys."+key)) {
							key = plugin.FormatKey();
						}
						plugin.getConfig().set("keys."+key, grupo+","+item4.split(",")[1]);
						plugin.saveConfig();
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"Key: "+ChatColor.GREEN+key+ChatColor.WHITE+" ("+grupo.toUpperCase()+") - "+ChatColor.GREEN+item4.split(",")[1]+ChatColor.WHITE+" "+plugin.getMessage("message1")+".");
						plugin.reloadConfig();
					}
					else {
						ThreadVZ nk = new ThreadVZ(plugin,"newkey",sender,grupo,Integer.parseInt(item4.split(",")[1]),key);
						nk.start();
					}
				}
		}
	}
	
	/*public void testNVP() {
		Profile user = new BaseProfile.Builder(plugin.getConfig().getString("paypal.username"),plugin.getConfig().getString("paypal.password")).signature(plugin.getConfig().getString("paypal.signature")).build();
		paypalnvp.core.PayPal pp = new paypalnvp.core.PayPal(user,paypalnvp.core.PayPal.Environment.SANDBOX);
		GetTransactionDetails tr = new GetTransactionDetails(plugin.getConfig().getString("paypal.transaction_id"));
		pp.setResponse(tr);
		if(tr.getNVPResponse().get("PAYMENTSTATUS").equals("Completed")) {//L_NAME0    L_QTY0   = nome e quantidade do item 0
			plugin.getLogger().info("ta pago");
		}
		List<String> itens = new ArrayList<String>();
		for(String key : tr.getNVPResponse().keySet())
			if(key.startsWith("L_NAME")) {
				if(tr.getNVPResponse().get(key).contains("(vz:"))
					for(int i=0;i<Integer.parseInt(tr.getNVPResponse().get("L_QTY"+key.charAt(key.length()-1)));i++)
						itens.add(tr.getNVPResponse().get(key).split("\\(vz:")[1].split("\\)")[0]);
			}
		if(itens.size()==0)
			plugin.getLogger().info("sem vips");
		else
			for(String s : itens)
				plugin.getLogger().info(s);
		for(String key : tr.getNVPResponse().keySet())
			plugin.getLogger().info("key: "+key+" / info: "+tr.getNVPResponse().get(key));
	}*/
}
