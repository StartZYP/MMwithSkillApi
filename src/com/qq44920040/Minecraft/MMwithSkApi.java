package com.qq44920040.Minecraft;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class MMwithSkApi extends JavaPlugin implements Listener, SkillPlugin {
    private String Msg;
    private HashMap<String,Integer[]> MobsInfo = new HashMap<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!(file.exists())){
            saveDefaultConfig();
        }
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        ReloadConfig();
        super.onEnable();
    }

    @EventHandler
    public void MobsDeathEvent(EntityDeathEvent var1){
        GiveExp(var1.getEntity(),var1.getEntity().getKiller());
    }

    private void GiveExp(LivingEntity livingEntity, Player player){
        if (player!=null){
            PlayerData playerData = SkillAPI.getPlayerData(player);
            int PlayerLevel = playerData.getMainClass().getLevel();
            String Mobname = livingEntity.getCustomName();
            System.out.println(Mobname);
            if (MobsInfo.containsKey(Mobname)){
                Integer[] Mobarray = MobsInfo.get(Mobname);
                int LevelCalculateVaule = LevelCalculateVaule(PlayerLevel,Mobarray[0],Mobarray[1]);
                playerData.getMainClass().giveExp(LevelCalculateVaule, ExpSource.MOB);
                player.sendMessage(Msg.replace("{Mobs}",Mobname).replace("{Exp}",String.valueOf(Mobarray[1])));
            }
        }
    }

    private int LevelCalculateVaule(int PlayerLevel,int MobLevel,int Exp){
        int Levelvaule = PlayerLevel-MobLevel;
        if (Levelvaule>=-5&&Levelvaule<=5){
            switch (Levelvaule){
                case 2:
                    return (int)(Exp*0.85);
                case 3:
                    return (int)(Exp*0.50);
                case 4:
                    return (int)(Exp*0.30);
                case 5:
                    return (int)(Exp*0.20);
                case -2:
                    return (int)(Exp*0.20+Exp);
                case -3:
                    return (int)(Exp*0.30+Exp);
                case -4:
                    return (int)(Exp*0.50+Exp);
                case -5:
                    return (int)(Exp*0.85+Exp);
                default:
                    return Exp;
            }

        }
        return 0;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("mws")&&sender.isOp()&&args.length==1&&args[0].equalsIgnoreCase("reload")){
            ReloadConfig();
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void ReloadConfig(){
        reloadConfig();
        Msg = getConfig().getString("Msg");
        Set<String> mines = getConfig().getConfigurationSection("MMwithSkillApi").getKeys(false);
        for (String temp:mines){
            System.out.println(temp);
            int Level = getConfig().getInt("MMwithSkillApi."+temp+".Level");
            int Exp = getConfig().getInt("MMwithSkillApi."+temp+".Exp");
            MobsInfo.put(temp.replace("-","."),new Integer[]{Level,Exp});
        }
    }

    @Override
    public void registerSkills(SkillAPI skillAPI) {

    }

    @Override
    public void registerClasses(SkillAPI skillAPI) {

    }
}
