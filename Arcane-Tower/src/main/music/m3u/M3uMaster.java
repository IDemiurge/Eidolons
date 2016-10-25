package main.music.m3u;

import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.entity.MusicList;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;

import java.io.File;

public class M3uMaster {

	public static void backup() {

	}

	public static void exportListsIntoFolder(String path, PROPERTY p, boolean empty) {
		for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
			if (type.checkProperty(AT_PROPS.MUSIC_TAGS))
				continue;
			empty = false;
			String filepath = path;
			String content = "";
			if (!empty)
				try {
					content = M3uGenerator.getM3uForList(new MusicList(type));
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			if (p != null) {
				if (p.isContainer()) {
					for (String sub : StringMaster.openContainer(type.getProperty(p))) {
						FileManager.write(content, path + sub + "\\" + type.getName() + ".m3u");
					}
					continue;
				} else {
					filepath += (type.getProperty(p)).replace(";", "") + "\\";
				}
			}
			FileManager.write(content, filepath + type.getName() + ".m3u");
		}

	}

	public static void processMetaListFolder(String path, PROPERTY p) {
		for (File dir : FileManager.getFilesFromDirectory(path, true)) {
			for (File list : FileManager.getFilesFromDirectory(dir.getPath(), false)) {
				ObjType type = DataManager.getType(StringMaster.cropFormat(list.getName()),
						AT_OBJ_TYPE.MUSIC_LIST);
				if (p.isContainer())
					type.addProperty(p, dir.getName(), true, true);
				else
					type.setProperty(p, dir.getName());

			}
		}
		MusicCore.saveAll();
	}
}
