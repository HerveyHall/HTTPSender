package sender.console;

import sender.form.*;
import sender.socket.*;

public class ConsoleMain {
	private final static String USEAGE = "�÷���\r\n\t����1��GET/POST����\r\n\t����2��url\r\n\t����3��HTTPЭ�鼰�汾��",
			METHOD_ERROR = "HTTPЭ��ֻ֧��GET������POST����",
			INSTRUCTION = "ָ���\r\n\t���뱨��ͷ ��ah <�ֶ�> <ֵ>\r\n\t���뱨�����ݣ� ac <�ֶ�> <ֵ>\r\n\tɾ������ͷ�� dh <�ֶ�>\r\n\tɾ���������ݣ�dc <�ֶ�>\r\n\t��ȡ��ʱʱ�䣺time get\r\n\t���ó�ʱʱ�䣺time set <������>\r\n\t���ͱ��ģ�send <����>\r\n\t�˳�����exit",
			INS_TIP = "��������ȷ��ָ��", SUCCESS = "�ɹ�", FAILED = "ʧ��", TIME = "ʱ��", USETIME = "��ʱ", TIMEOUT = "����ʱ",
			SEND = "����", ADD = "���", DEL = "ɾ��", UPD = "�޸�", EXIT = "��лʹ�ã��ټ���", MS = "ms";

	static int time = 10000;// ���ձ��ĳ�ʱʱ��10��

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			Console.writeError(USEAGE);
			System.exit(-1);
		}
		HttpRequestPacket packet = new HttpRequestPacket();
		switch (args[0].toLowerCase()) {
		case "post":
			packet.setMethod(HttpRequestMethod.POST);
			break;
		case "get":
			packet.setMethod(HttpRequestMethod.GET);
			break;
		default:
			Console.writeError(METHOD_ERROR);
			System.exit(-2);
			break;
		}
		packet.setUrl(args[1]);
		packet.setProtocol(args[2]);
		Console.writeInfo(INSTRUCTION);
		Console.writeRow();
		label: while (true) {
			String cmd = Console.readLine();
			String[] ctrls = cmd.split("\\s");
			try {
				switch (ctrls[0]) {
				case "ah":
					for (int i = 3; i < ctrls.length; ++i)
						ctrls[i - 1] += ctrls[i] + " ";
					if (packet.addHeadText(new HeadText(ctrls[1], ctrls[2].trim())))
						Console.writeInfo(ADD + SUCCESS);
					else
						Console.writeError(ADD + FAILED);
					break;
				case "ac":
					for (int i = 3; i < ctrls.length; ++i)
						ctrls[i - 1] += ctrls[i] + " ";
					if (packet.addContentText(new ContentText(ctrls[1], ctrls[2].trim())))
						Console.writeInfo(ADD + SUCCESS);
					else
						Console.writeError(ADD + FAILED);
					break;
				case "dh":
					if (packet.deleteHeadText(new HeadText(ctrls[1], "")))
						Console.writeInfo(DEL + SUCCESS);
					else
						Console.writeError(DEL + FAILED);
					break;
				case "dc":
					if (packet.deleteContentText(new ContentText(ctrls[1], "")))
						Console.writeInfo(DEL + SUCCESS);
					else
						Console.writeError(DEL + FAILED);
					break;
				case "time":
					if (ctrls.length > 1)
						switch (ctrls[1]) {
						case "get":
							Console.writeInfo(TIMEOUT + TIME + time + MS);
							break;
						case "set":
							if (ctrls.length > 2)
								try {
									time = Integer.parseInt(ctrls[2]);
									Console.writeInfo(UPD + SUCCESS);
								} catch (NumberFormatException e) {
									Console.writeError(INS_TIP);
								}
							else
								Console.writeError(INS_TIP);
							break;
						default:
							Console.writeError(INS_TIP);
							break;
						}
					else
						Console.writeError(INS_TIP);
					break;
				case "send":
					try {
						PacketSender sender = new SenderConfig(ctrls[1], 80).getNewSender();
						if (sender.send(packet))
							Console.writeInfo(SEND + SUCCESS);
						else {
							Console.writeError(SEND + FAILED);
							continue label;
						}
						int tempTime = time;
						while (!sender.getStatus() && tempTime-- > 1)
							Thread.sleep(1);
						if (tempTime == 0)
							Console.writeError(TIMEOUT);
						Console.writeInfo("\r\n" + sender.getResponse());
						Console.writeInfo(USETIME + (time - tempTime) + MS);
						Console.writeInfo(EXIT);
						System.exit(0);
					} catch (Exception e) {
						Console.writeError(SEND + FAILED);
						continue label;
					}
				case "exit":
					Console.writeInfo(EXIT);
					System.exit(0);
					break;
				default:
					Console.writeError(INS_TIP);
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				Console.writeError(INS_TIP);
				continue label;
			} finally {
				Console.writeRow();
			}
		}
	}

}
