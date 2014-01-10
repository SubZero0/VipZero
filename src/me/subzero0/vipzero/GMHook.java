package me.subzero0.vipzero;

import java.util.Arrays;
import java.util.List;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
 
public class GMHook
{
	private GroupManager groupManager;
	private Plugin plugin;
 
	public GMHook(final Plugin plugin)
	{
		this.plugin = plugin;
	}
 
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(final PluginEnableEvent event)
	{
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
 
		if (GMplugin != null && GMplugin.isEnabled())
		{
			groupManager = (GroupManager)GMplugin;
 
		}
	}
 
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event)
	{
		if (groupManager != null)
		{
			if (event.getPlugin().getDescription().getName().equals("GroupManager"))
			{
				groupManager = null;
			}
		}
	}
 
	public String getGroup(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getGroup(base.getName());
	}
 
	public boolean setGroup(final Player base, final String group)
	{
		final OverloadedWorldHolder handler = groupManager.getWorldsHolder().getWorldData(base);
		if (handler == null)
		{
			return false;
		}
		handler.getUser(base.getName()).setGroup(handler.getGroup(group));
		return true;
	}
 
	public List<String> getGroups(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return null;
		}
		return Arrays.asList(handler.getGroups(base.getName()));
	}
 
	public String getPrefix(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserPrefix(base.getName());
	}
 
	public String getSuffix(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserSuffix(base.getName());
	}
 
	public boolean hasPermission(final Player base, final String node)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return false;
		}
		return handler.has(base, node);
	}
}