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

import br.com.uol.pagseguro.domain.AccountCredentials;
import br.com.uol.pagseguro.domain.Transaction;
import br.com.uol.pagseguro.service.TransactionSearchService;

public class PagSeguro extends Thread {
	private Main plugin;
	private String transactionCode = "";
	Transaction transaction = null;
	CommandSender sender = null;
	public PagSeguro(Main plugin,String transactionCode2,CommandSender cmds) {
		this.plugin = plugin;
		transactionCode=transactionCode2.toUpperCase();
		sender = cmds;
	}
	
	public void run() {
		try {
            transaction = TransactionSearchService.searchByCode(new AccountCredentials(plugin.getConfig().getString("pagseguro.email"),plugin.getConfig().getString("pagseguro.token")), transactionCode);
        } catch (Exception e) {
        	sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error15")+"!");
            plugin.using_ps.remove(transactionCode);
            e.printStackTrace();
            return;
        }
        if (transaction==null) {
        	sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error15")+"!");
        	plugin.using_ps.remove(transactionCode);
        	return;
        }
        //printTransaction(transaction);
		if(transaction.getStatus().getValue()!=4&&transaction.getStatus().getValue()!=3) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error16")+"!");
			plugin.using_ps.remove(transactionCode);
			return;
		}
		List<String> itens = new ArrayList<String>();
		for(Object item : transaction.getItems()) {
			String desc = item.toString().split("id: ")[1].split(",")[0];
			if(desc.contains("vz:"))
				itens.add(item.toString());
		}
		if(itens.size()==0||itens==null) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error17")+"!");
			plugin.using_ps.remove(transactionCode);
			return;
		}
		boolean pedido_valido = true;
		for(String item2 : itens) {
			boolean achou = false;
			for(String gName : plugin.getConfig().getStringList("vip_groups"))
				if(gName.trim().equalsIgnoreCase(item2.split("id: vz:")[1].split(",")[0]))
					achou=true;
			if(!achou) {
				pedido_valido=false;
				break;
			}
			int days = Integer.parseInt(item2.split("id: vz:")[1].split(",")[1]);
			if(days<0||days>10000) {
				pedido_valido=false;
				break;
			}
		}
		if(!pedido_valido) {
			sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+plugin.getMessage("error18")+"!");
			return;
		}
		
		plugin.pagseguro.set(transactionCode+".usedBy", sender.getName());
		Calendar now = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		plugin.pagseguro.set(transactionCode+".date", fmt.format(now.getTime()));
		try {
			plugin.pagseguro.save(new File(plugin.getDataFolder(),"pagseguro.yml"));
		} catch (IOException e) {e.printStackTrace();}
		if(!plugin.flatfile&&plugin.mysql_pagseguro) {
			try {
				Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
				PreparedStatement addlog = con.prepareStatement("INSERT INTO `vipzero_pagseguro` (`key`,`nome`,`data`) VALUES ('"+transactionCode+"','"+sender.getName()+"','"+fmt.format(now.getTime())+"');");
				addlog.execute();
				addlog.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		plugin.using_ps.remove(transactionCode);
		
		if(plugin.getConfig().getInt("pagseguro.mode")==1) {
			//dá direto
			for(String item3 : itens)
				for(int i=0;i<Integer.parseInt(item3.split("quantity: ")[1].split(",")[0]);i++) {
					String grupo = "";
					for(String gName : plugin.getConfig().getStringList("vip_groups"))
						if(gName.trim().equalsIgnoreCase(item3.split("id: vz:")[1].split(",")[0])) {
							grupo=gName.trim();
							break;
						}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), (plugin.getLanguage().trim().equalsIgnoreCase("br")?"darvip":"givevip")+" "+sender.getName()+" "+grupo+" "+item3.split("id: vz:")[1].split(",")[1]);
				}
		}
		else /*if(plugin.getConfig().getInt("pagseguro.mode")==2)*/ {
			//dá keys
			for(String item4 : itens)
				for(int i=0;i<Integer.parseInt(item4.split("quantity: ")[1].split(",")[0]);i++) {
					String grupo = "";
					for(String gName : plugin.getConfig().getStringList("vip_groups"))
						if(gName.trim().equalsIgnoreCase(item4.split("id: vz:")[1].split(",")[0])) {
							grupo=gName.trim();
							break;
						}
					String key = plugin.FormatKey();
					if(plugin.flatfile) {
						while(plugin.getConfig().contains("keys."+key)) {
							key = plugin.FormatKey();
						}
						plugin.getConfig().set("keys."+key, grupo+","+item4.split("id: vz:")[1].split(",")[1]);
						plugin.saveConfig();
						sender.sendMessage(ChatColor.AQUA+"["+plugin.getConfig().getString("server_name").trim()+"] "+ChatColor.WHITE+"Key: "+ChatColor.GREEN+key+ChatColor.WHITE+" ("+grupo.toUpperCase()+") - "+ChatColor.GREEN+item4.split("id: vz:")[1].split(",")[1]+ChatColor.WHITE+" "+plugin.getMessage("message1")+".");
						plugin.reloadConfig();
					}
					else {
						ThreadVZ nk = new ThreadVZ(plugin,"newkey",sender,grupo,Integer.parseInt(item4.split("id: vz:")[1].split(",")[1]),key);
						nk.start();
					}
				}
		}
	}

    /*private static void printTransaction(Transaction transaction) {

        System.out.println("code: " + transaction.getCode());
        System.out.println("date: " + transaction.getDate());
        System.out.println("discountAmount: " + transaction.getDiscountAmount());
        System.out.println("extraAmount: " + transaction.getExtraAmount());
        System.out.println("feeAmount: " + transaction.getFeeAmount());
        System.out.println("grossAmount: " + transaction.getGrossAmount());
        System.out.println("installmentCount: " + transaction.getInstallmentCount());
        System.out.println("itemCount: " + transaction.getItemCount());

        for (int i = 0; i < transaction.getItems().size(); i++) {
            System.out.println("item[" + (i + 1) + "]: " + transaction.getItems().get(i));
        }

        System.out.println("lastEventDate: " + transaction.getLastEventDate());
        System.out.println("netAmount: " + transaction.getNetAmount());
        System.out.println("paymentMethodType: " + transaction.getPaymentMethod().getCode().getValue());
        System.out.println("paymentMethodcode: " + transaction.getPaymentMethod().getType().getValue());
        System.out.println("reference: " + transaction.getReference());
        System.out.println("senderEmail: " + transaction.getSender().getEmail());

        if (transaction.getSender() != null) {
            System.out.println("senderPhone: " + transaction.getSender().getPhone());
        }

        if (transaction.getShipping() != null) {
            System.out.println("shippingType: " + transaction.getShipping().getType().getValue());
            System.out.println("shippingCost: " + transaction.getShipping().getCost());
            if (transaction.getShipping().getAddress() != null) {
                System.out.println("shippingAddressCountry: " + transaction.getShipping().getAddress().getCountry());
                System.out.println("shippingAddressState: " + transaction.getShipping().getAddress().getState());
                System.out.println("shippingAddressCity: " + transaction.getShipping().getAddress().getCity());
                System.out.println("shippingAddressPostalCode: "
                        + transaction.getShipping().getAddress().getPostalCode());
                System.out.println("shippingAddressDistrict: " + transaction.getShipping().getAddress().getDistrict());
                System.out.println("shippingAddressStreet: " + transaction.getShipping().getAddress().getStreet());
                System.out.println("shippingAddressNumber: " + transaction.getShipping().getAddress().getNumber());
                System.out.println("shippingAddressComplement: "
                        + transaction.getShipping().getAddress().getComplement());
            }
        }

        System.out.println("status: " + transaction.getStatus().getValue());
        System.out.println("type: " + transaction.getType().getValue());
    }*/
}
