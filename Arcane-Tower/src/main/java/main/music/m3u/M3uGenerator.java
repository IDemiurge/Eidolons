package main.music.m3u;

import main.content.parameters.G_PARAMS;
import main.data.ability.construct.VariableManager;
import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.ahk.AHK_Master;
import main.music.entity.MusicList;
import main.music.entity.Track;
import main.swing.generic.components.editors.lists.ListObjChooser;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class M3uGenerator {
	public static final String M3U_PREFIX = "#EXTM3U";
	public static final String M3U_TRACK_PREFIX = "#EXTINF:";
	public static final String M3U_TRACK_PREFIX_UTF8 = "#EXTINFUTF8:";
	public final static String[] GENERATE_MUSIC_FOLDERS = { "K:\\2016\\music;", "I:\\``new mus",
			"I:\\`new mus", "I:\\New music", "I:\\newest music", "I:\\RES\\music",
			"K:\\`````new music", "K:\\````new music", "K:\\``new music", "K:\\`new mus",
			"K:\\New Music", "K:\\newest music", "D:\\``new music", "D:\\``X``\\xFiles",
			"D:\\``X``\\zFiles", "D:\\`new music", "D:\\Sounds", "G:\\Music&other fun",
			"X:\\``new music", "X:\\`new mus", "X:\\Music", "X:\\NEW MUSIC" };

	public static void generateCustomM3Us() {
		for (String s : (GENERATE_MUSIC_FOLDERS)) {
			List<File> dirs = FileManager.getFilesFromDirectory(s, true);
			for (File dir : dirs) {
				String folderName = StringMaster.getPathSegments(dir.getPath()).get(0).replace(":",
						"");
				// StringMaster.getPathSegments(dir.getName()).get(
				// StringMaster.getPathSegments(dir.getName()).size() - 2);
				//
				List<String> filePaths = getTracks(dir);

				// recursion: get a list of paths from all the way down!
				listToM3U(filePaths, folderName, getGeneratedListName(dir.getName()));
			}
		}
	}

	public static void listToM3U(List<String> filePaths, String folderName, String listName) {
		String content = getM3uForTracks(filePaths, folderName);// TODO what if
																// I want to
																// merge ?

		FileManager.write(content, AHK_Master.GENERATED_LISTS_FOLDER + folderName + "\\" + listName
				+ ".m3u");
	}

	public static String getM3uForTracks(List<Track> filePaths) {
		String content = M3U_PREFIX + StringMaster.NEW_LINE;
		ListMaster.removeNullElements(filePaths);
		for (Track sub : filePaths) {
			String param = sub.getParam(G_PARAMS.DURATION);
			String path = sub.getProperty(AT_PROPS.PATH);
			String property = sub.getProperty(AT_PROPS.ARTIST);
			if (property.isEmpty())
				property = StringMaster.getPathSegments(path).get(
						StringMaster.getPathSegments(path).size() - 2);
			content += M3U_TRACK_PREFIX + param + "," + property + " - " + sub.getName()
					+ StringMaster.NEW_LINE;

			content += path + StringMaster.NEW_LINE;

		}
		return content;
	}

	public static String getM3uForTracks(List<String> filePaths, String folderName) {
		String content = M3U_PREFIX + StringMaster.NEW_LINE;
		for (String sub : filePaths) {
			content += M3U_TRACK_PREFIX + "100," + folderName + " - "
					+ StringMaster.getLastPathSegment(sub) + StringMaster.NEW_LINE;
			content += (sub) + StringMaster.NEW_LINE;

		}
		return content;
	}

	public static void repairM3uLists() {
		List<MusicList> lists = new ListObjChooser<MusicList>().selectMulti(MusicCore
				.getMusicLists());

		for (MusicList list : lists) {
			List<Track> tracks = list.getTracks();
			List<Track> cleanTracks = new ListMaster<Track>().getRemovedDuplicates(tracks);

			List<Track> differingElements = new ListMaster<Track>().getDifferingElements(tracks,
					cleanTracks);
			main.system.auxiliary.LogMaster.log(1, list + " has differingElements "
					+ differingElements);
			main.system.auxiliary.LogMaster.log(1, list + " Clean Tracks: " + cleanTracks);

			list.setTracks(cleanTracks);
			MusicCore.saveList(list);
		}
	}

	public static void removeDuplicatesFromList(MusicList list) {
		new ListMaster<Track>().removeDuplicates(list.getTracks());

	}

	public static List<String> getTracks(File dir) {
		List<String> filePaths = new LinkedList<>();
		addMusicTracks(dir, filePaths);
		if (filePaths.isEmpty())
			return new LinkedList<>();
		return filePaths;
	}

	public static String getM3uForList(MusicList musicList) {
		return getM3uForTracks(musicList.getTracks());
	}

	private static String getGeneratedListName(String folderName) {
		String[] array = folderName.split("-");
		String name = "";
		for (String a : array) {
			a = a.replace("_", "");
			a = a.trim();
			if (StringMaster.isInteger(a))
				continue;
			if (StringMaster.getFirstNumberIndex(a) < 2)
				continue;
			a = VariableManager.removeVarPart(a);
			if (a.length() > 20)
				continue;
		}
		if (name.isEmpty())
			name = array[0];
		return StringMaster.getWellFormattedString(name);
	}

	private static void addMusicTracks(File dir, List<String> filePaths) {
		for (File sub : FileManager.getFilesFromDirectory(dir.getPath(), true)) {
			if (sub.isDirectory())
				addMusicTracks(sub, filePaths);
			else if (FileManager.isMusicFile(sub))
				filePaths.add(sub.getPath());
		}

	}

}
