package net.mpoisv.kakaoschedule;

import android.database.Cursor;

import net.mpoisv.kakaoschedule.net.mpoisv.kakaoschedule.db.DataBaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ResponseKakao {

    public static DataBaseHelper dataBaseHelper;

    public static void response(String room, String msg, String sender, boolean isGroupChat, Replier replier) {
        if(!isBotCommand(msg)) return;
        if(dataBaseHelper == null) {
            replier.reply("ERROR:\nDB에 연결되어있지 않습니다.");
            return;
        }

        if(msg.startsWith(".s ") || msg.equals(".s")) {
            Calendar date = getDay(msg);

            if(date == null) {
                replier.reply("ERROR:\n시간을 정확히 입력하세요.\n\nex) 0101 -> 1월 1일");
                return;
            }

            HashMap<String, ArrayList<String>> data = dataBaseHelper.getData(date.getTimeInMillis());

            if(data.size() == 0) {
                replier.reply(getTimeString(date) + "\n" +
                        "\n등록된 일정이 없습니다.\n");
                return;
            }

            StringBuffer buf = new StringBuffer();

            for (String sub : data.keySet()) {
                buf.append("\n" + sub);
                int i = 0;
                for (String items : data.get(sub))
                    buf.append("\n  " + (++i) + ". " + items);
            }

            replier.reply(getTimeString(date) + "\n" + buf.toString());
        }
        else if(msg.startsWith(".add ") || msg.equals(".add")) {
            Calendar date = getDay(msg);

            if(date == null) {
                replier.reply("ERROR:\n시간을 정확히 입력하세요.\n\nex) 0101 -> 1월 1일");
                return;
            }
            String[] list = msg.split(" ");

            int isUntil = 0;

            int subjectCode = -1;
            int index = 1;


            if(list.length >= 3) {
                if(list[1].contains("-") && list[1].contains("u")) {
                    isUntil = 1;
                    index = 2;
                }

                try {
                    Integer.parseInt(list[index]);
                    if(list[index].length() == 4) index++;
                }catch (Exception e) { }

                if(list.length - 1 < index + 1) {
                    replier.reply("ERROR:\n명령어를 정확히 입력하세요.");
                    return;
                }

                subjectCode = getSubjectCode(list[index++]);

                if(subjectCode == -1) {
                    replier.reply("ERROR:\n과목을 정확히 입력하세요.");
                    return;
                }

                StringBuilder buf = new StringBuilder();
                for(int i = index; i < list.length; i++) {
                    buf.append(list[i]);
                    if(i < list.length - 1)
                        buf.append(" ");
                }

                if(dataBaseHelper.insertData(subjectCode, sender, buf.toString(), date.getTimeInMillis(), isUntil))
                    replier.reply(getTimeString(date) + "\n" +
                            "과목: " + getSubjectString(subjectCode) +"\n\n" +
                            "추가된 일정: " + buf.toString());
            }else
                replier.reply("ERROR:\n명령어를 정확히 입력하세요.");

        }else if(msg.startsWith(".rm ") || msg.equals(".rm")) {
            Calendar date = getDay(msg);

            if(date == null) {
                replier.reply("ERROR:\n시간을 정확히 입력하세요.\n\nex) 0101 -> 1월 1일");
                return;
            }

            int index = 1;
            int line;
            String[] list = msg.split(" ");

            if(list.length >= 3) {
                if (list[1].contains("-") && list[1].contains("u")) {
                    index = 2;
                }

                try {
                    Integer.parseInt(list[index]);
                    if (list[index].length() == 4) index++;
                } catch (Exception e) {
                }

                if (list.length - 1 < index + 1) {
                    replier.reply("ERROR:\n명령어를 정확히 입력하세요.");
                    return;
                }

                int subject = getSubjectCode(list[index++]);

                if(subject == -1) {
                    replier.reply("ERROR:\n삭제할 과목을 정확히 입력하세요.");
                    return;
                }

                try {
                    line = Integer.parseInt(list[index]);
                }catch (Exception e) {
                    replier.reply("ERROR:\n삭제할 줄을 정확히 입력하세요.");
                    return;
                }

                int data = dataBaseHelper.deleteData(subject, line, date.getTimeInMillis());
                if(data == 0) {
                    replier.reply("ERROR:\n삭제되지 않았습니다.");
                    return;
                }
                replier.reply(getTimeString(date) + "\n\n" +
                        "과목: "+getSubjectString(subject) + "\n" +
                        "삭제된 줄: "+line);
            }
        }
    }

    private static String getTimeString(Calendar date) {
        return (date.get(Calendar.MONTH) + 1) + "월 " + date.get(Calendar.DATE) + "일 (" + new String[]{"토", "일", "월", "화", "수", "목", "금"}[date.get(Calendar.DAY_OF_WEEK)] + ")";
    }

    private static boolean isBotCommand(String msg) {
        return (msg.startsWith(".s ") || msg.equals(".s") ||
                msg.startsWith(".add ") || msg.equals(".add") ||
                msg.startsWith(".rm ") || msg.equals(".rm"));
    }

    private static int getSubjectCode(String msg) {
        if(msg.contains("국어") || msg.equals("1"))
            return 1;
        if(msg.contains("디자인") || msg.contains("디일") || msg.equals("2"))
            return 2;
        if(msg.contains("수학") || msg.equals("3"))
            return 3;
        if(msg.contains("영어") || msg.equals("4"))
            return 4;
        if(msg.contains("음감비") || msg.contains("음악") || msg.contains("감상") || msg.contains("비평") || msg.equals("5"))
            return 5;
        if(msg.contains("정통") || msg.contains("정보") || msg.contains("통신") || msg.equals("6"))
            return 6;
        if(msg.contains("진로") || msg.equals("7"))
            return 7;
        if(msg.contains("체육") || msg.contains("체") || msg.equals("8"))
            return 8;
        if(msg.contains("컴구") || msg.contains("컴퓨터구조") || msg.contains("컴뷰터구조") || msg.contains("구조") || msg.equals("9"))
            return 9;
        if(msg.contains("컴네") || msg.contains("켐퓨터네트워크") || msg.contains("컴퓨터네트워크") || msg.contains("네트워크") || msg.contains("네트") || msg.equals("10"))
            return 10;
        if(msg.contains("통사") || msg.contains("사회") || msg.contains("통합사회") || msg.equals("11"))
            return 11;
        if(msg.contains("통과") || msg.contains("통합과학") || msg.contains("과학") || msg.equals("12"))
            return 12;
        if(msg.contains("프밍") || msg.contains("프로그래밍") || msg.contains("프로그램") || msg.contains("프") || msg.equals("13"))
            return 13;

        return -1;
    }

    public static String getSubjectString(int code) {
        switch (code) {
            case 1:
                return "국어";
            case 2:
                return "디자인 일반";
            case 3:
                return "수학";
            case 4:
                return "영어";
            case 5:
                return "음악 감상과 비평";
            case 6:
                return "정보통신";
            case 7:
                return "진로";
            case 8:
                return "체육";
            case 9:
                return "컴퓨터 구조";
            case 10:
                return "컴퓨터 네트워크";
            case 11:
                return "통합 사회";
            case 12:
                return "통합 과학";
            case 13:
                return "프로그래밍";
        }

        return "";
    }

    private static Calendar getDay(String msg) {
        String[] list = msg.split(" ");

        Calendar date = Calendar.getInstance();

        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);

        if(list.length >= 2) {
            if(list[1].contains("-")) {
                if(list[1].contains("n")) {
                    return date;
                }
                else if(list[1].contains("u")) {
                    if(list.length >= 3) {
                        try {
                            Integer.parseInt(list[2]);
                            if(list[2].length() == 4) {
                                date.set(Calendar.MONTH, Integer.parseInt(list[2].substring(0, 2)) - 1);
                                date.set(Calendar.DATE, Integer.parseInt(list[2].substring(2, 4)));

                                return date;
                            }
                        } catch (Exception e) {
                            return date;
                        }
                    }else {
                        return date;
                    }
                }
            }else {
                try {
                    Integer.parseInt(list[1]);
                    if(list[1].length() == 4) {
                        date.set(Calendar.MONTH, Integer.parseInt(list[1].substring(0, 2)) - 1);
                        date.set(Calendar.DATE, Integer.parseInt(list[1].substring(2, 4)));

                        return date;
                    }
                    if(list[1].length() <= 2)
                        return date;
                } catch (Exception e) {
                    return date;
                }
            }
        }else {
            return date;
        }

        return null;
    }
}
