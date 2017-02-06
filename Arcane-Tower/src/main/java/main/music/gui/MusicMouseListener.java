package main.music.gui;

import main.music.MusicCore;
import main.music.ahk.AHK_Master;
import main.music.entity.MusicList;
import main.music.entity.Track;
import main.music.m3u.M3uGenerator;
import main.swing.generic.components.editors.lists.EnumListChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MusicMouseListener extends MouseClickListener implements ActionListener {
	private static CLICK_MODE clickMode;
	private static PLAY_MODE playMode;
	private static String arg;
	private static MusicList selectedList;
	private static List<MusicList> selectedLists = new LinkedList<>();
	private static List<Track> selected = new LinkedList<>();
    private static Container activePanel;
    MusicList list;
    private String function;
    private String keyPart;
    private MusicList previousSelectedList;
    private String name;

	public MusicMouseListener(String name, String keyPart, String funcPart) {
		this.keyPart = keyPart;
		this.name = name;
		function = funcPart.replaceFirst("Run ", "");
        if (MusicCore.initMusicListTypes) {
            getList();
        }
        MusicCore.getListenerMap().put(name, this);
	}

    public static void playM3uList(String listPath, PLAY_MODE playMode) {
        File file = new File(listPath);
        if (playMode != null) {
            if (playMode != PLAY_MODE.NORMAL) {
                List<Track> tracks = MusicCore.getTracks(listPath);// getList()
                String newPath = AHK_Master.SYSTEM_LISTS_FOLDER + "Play Mode Gen\\ "
                        + playMode.toString() + " "
                        + StringMaster.getLastPathSegment(StringMaster.cropFormat(listPath))
                        // getList().getName()
                        + ".m3u";
                tracks = getTracksForPlayMode(playMode, tracks);
                String content = M3uGenerator.getM3uForTracks(tracks);
                FileManager.write(content, newPath);
                file = new File(newPath);
            }
        }
        try {
            Desktop.getDesktop().open(file);
            MusicList playList = MusicCore.findList(StringMaster.cropFormat(file.getName()));
            if (!MusicCore.getLastPlayed().contains(playList)) {
                MusicCore.getLastPlayed().add(playList);
            }
            AHK_Master.getPanel().getControlPanel().refresh();
            // Runtime.getRuntime().exec(function.replaceFirst("Run ", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Track> getTracksForPlayMode(PLAY_MODE playMode, List<Track> tracks) {
        switch (playMode) {
            case INTERLEAVE:
                return new ListMaster<Track>().interleave(tracks, 1);
            case MERGE_SWAP:
                break;
            case MERGE_SHUF_TOP:
            case MERGE_TOP:
                Integer toIndex = DialogMaster.inputInt(14 / getSelectedLists().size());
                for (MusicList sub : getSelectedLists()) {
                    tracks.addAll(sub.getTracks().subList(0, toIndex));
                }
                if (playMode == PLAY_MODE.MERGE_SWAP) {
                    Collections.shuffle(tracks);
                }

                return tracks;
            case MERGE_SHUF:
                for (MusicList sub : getSelectedLists()) {
                    tracks.addAll(sub.getTracks());
                }
                Collections.shuffle(tracks);
                return tracks;
            case REVERSE:
                Collections.reverse(tracks);
                return tracks;
            case SHUFFLE:
                Collections.shuffle(tracks);
                return tracks;

        }
        return tracks;
    }

    public static CLICK_MODE getMode() {
        return clickMode;
    }

    public static void setMode(CLICK_MODE mode) {
        MusicMouseListener.clickMode = mode;
    }

    public static MusicList getSelectedList() {
        return selectedList;
    }

    public static void setSelectedList(MusicList selectedList) {
        MusicMouseListener.selectedList = selectedList;
    }

    public static List<Track> getSelectedTracks() {
        return selected;
    }

    public static String getArg() {
        return arg;
    }

    public static void setArg(String arg) {
        MusicMouseListener.arg = arg;
    }

    public static Container getActivePanel() {
        return activePanel;
    }

    public static void setActivePanel(Container activePanel) {
        MusicMouseListener.activePanel = activePanel;

    }

    public static List<MusicList> getSelectedLists() {
        return selectedLists;
    }

    public static CLICK_MODE getClickMode() {
        return clickMode;
    }

    public static void setClickMode(CLICK_MODE tag) {
        // if (tag == getMode())
        // setMode(null);
        // else
        setMode(tag);
    }

    public static PLAY_MODE getPlayMode() {
        return playMode;
    }

    public static void setPlayMode(PLAY_MODE playMode) {
        MusicMouseListener.playMode = playMode;
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		setSelectedList();
		Component button = (Component) arg0.getSource();
		setActivePanel((button.getParent()));
        if (MathMaster.isMaskAlt(arg0.getModifiers())) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(arg0)) {
			handleClick(isInvertCtrl() ? 0 : ActionEvent.CTRL_MASK, CLICK_MODE.EDIT_TRACKS);
		}
	}

	private boolean isInvertCtrl() {
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setSelectedList();
		Component button = (Component) arg0.getSource();
		setActivePanel((button.getParent()));
		int modifiers = arg0.getModifiers();
		if (MathMaster.isMaskAlt(modifiers)) {
			clickMode = new EnumListChooser<CLICK_MODE>().chooseEnumConst(CLICK_MODE.class);
            if (!MathMaster.isCtrlMask(modifiers)) {
                modifiers = 0;
            }

            if (clickMode == null) {
                return;
            }
        }

		// else
		// mode = AHK_Master.getMode();
		handleClick(modifiers, clickMode);
	}

	private void setSelectedList() {
		previousSelectedList = selectedList;
		selectedList = getList();
	}

	private String pickArg(CLICK_MODE mode) {
		switch (mode) {
			case TYPE:
				return pick(true);
			case TAG:
				return pick(false);

		}
		return null;
	}

	private String pick(boolean group) {
		List<String> defaults = MusicCore.getMusicConsts(group);
		// add custom
		String selected = group ? ListChooser.chooseString(new LinkedList<>(defaults))
				: ListChooser.chooseString(new LinkedList<>(defaults));
        if (selected == null) {
            return DialogMaster.inputText("Input custom...", "");
        }

		return selected;
	}

	public void handleClick(int modifiers, CLICK_MODE mode) {

		boolean alt = MathMaster.isMaskAlt(modifiers);
		boolean ctrl = MathMaster.isCtrlMask(modifiers);
		boolean shift = MathMaster.isShiftMask(modifiers);
        if (isInvertCtrl()) {
            ctrl = !ctrl;
        }
        if (!ctrl) {
            arg = null;
        }
        if (mode == null) {
            mode = clickMode;
        }
        if (mode != null) {
            if (arg == null) {
                arg = pickArg(mode);
            }
        }
        if (checkPlayModeForce()) {
			if (!ctrl) {
				play(alt);
				return;
			}

			mode = getCtrlMode(playMode);
		}

		if (mode == CLICK_MODE.PICK_TRACKS) {
            if (alt) {
                selected.addAll(getList().getTracks());
            } else {
                selected.addAll(getList().selectTracks());
            }
            selectedLists.clear();
			AHK_Master.getPanel().getDisplayPanel().refresh();
			return;

		}

		if (mode == CLICK_MODE.PICK_LIST) {
			selectedLists.add(getList());
			AHK_Master.getPanel().getDisplayPanel().refresh();
			selected.clear();
			return;
		}
		if (mode == CLICK_MODE.EDIT) {
			MC_ControlPanel.doEdit(getList(), alt);
		} else if (mode == CLICK_MODE.TAG) {
			getList().addTag(arg);
			SoundMaster.playScribble();

		} else if (mode == CLICK_MODE.GENRE) {
			getList().setGenre(arg);
			SoundMaster.playScribble();
		} else if (mode == CLICK_MODE.TYPE) {
			getList().setMusicType(arg);
			SoundMaster.playScribble();
		} else if (mode == CLICK_MODE.EDIT_TRACKS) {

			if (alt) {
				if (!getSelectedTracks().isEmpty()) {
					if (DialogMaster.confirm("Add selected Tracks to " + getList() + "?")) {
						getList().addTracks(getSelectedTracks());
						MusicCore.saveList(getList());
						return;
					}
				} else {
					if (!getSelectedLists().isEmpty() || getSelectedList() != null) {
						if (DialogMaster.confirm("Add Tracks from selected lists to " + getList()
								+ "?")) {
							getList().addTracks(
									MusicCore.getTracksFromLists(getSelectedLists().toArray(
											new MusicList[getSelectedLists().size()])));
							getList().addTracks(
									MusicCore.getTracksFromLists(getPreviousSelectedList()));

							MusicCore.saveList(getList());
							return;
						}
					}
				}
			}
			getList().editTracks();
			SoundMaster.playScribble();
			return;
		} else {

			play(alt);
		}

		selected.clear();
		selectedLists.clear();
		AHK_Master.getPanel().getDisplayPanel().refresh();
	}

	private CLICK_MODE getCtrlMode(PLAY_MODE playMode) {
        if (playMode != null) {
            switch (playMode) {
                case MERGE_SHUF_TOP:
                case MERGE_SWAP:
                case MERGE_SHUF:
                case MERGE_TOP:
                    return CLICK_MODE.PICK_LIST;
            }
        }
        return clickMode;
	}

	private boolean checkPlayModeForce() {
        if (playMode != null) {
            switch (playMode) {
                case MERGE_SHUF_TOP:
                case MERGE_SWAP:
                case MERGE_SHUF:
                case MERGE_TOP:
                    return true;

                case INTERLEAVE:
                    break;
                case NORMAL:
                    break;
                case REVERSE:
                    break;
                case SHUFFLE:
                    break;
                default:
                    break;
            }
        }
        return false;
	}

	private void play(boolean alt) {
		getList();
		playM3uList(function, playMode);
        if (alt) {
            DialogMaster.confirm("Keys: " + AHK_Master.getKeyModifiers(keyPart));
        }
    }

	public MusicList getPreviousSelectedList() {
		return previousSelectedList;
	}

	public MusicList getList() {
        if (list == null) {
            list = MusicCore.getList(name, keyPart, function);
        }
        list.setMouseListener(this);
		return list;
	}

    public enum PLAY_MODE {
        NORMAL, SHUFFLE, REVERSE, INTERLEAVE, MERGE_SWAP, MERGE_SHUF, MERGE_TOP, MERGE_SHUF_TOP,

	}

    public enum CLICK_MODE {
        PLAY, TAG, TYPE, GENRE, EDIT, EDIT_TRACKS, PICK_TRACKS, PICK_LIST,

        // VIEW_TRACKS, SELECT_TRACKS,
    }

}
