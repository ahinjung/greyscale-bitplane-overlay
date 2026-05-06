import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Bitplane {
	public static void main(String[] args) throws Exception {
		// 1. 이미지 업로드
		File file = new File("myPhotoDark.jpg");
		BufferedImage image = ImageIO.read(file);
		int w = image.getWidth();
		int h = image.getHeight();
		
		// 2. 그레이스케일로 변환하고 저장
		BufferedImage grayImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(image.getRGB(x, y));
                int gray = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                grayImage.getRaster().setSample(x, y, 0, gray);
            }
        }
        
        // 3. 8개의 비트플레인 이미지로 분해
        BufferedImage[] bitplanes = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            bitplanes[i] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int gray = grayImage.getRaster().getSample(x, y, 0);
                    // 비트 연산
                    int bit = (gray >> i) & 1;
                    // 4. 출력용 변환 (bit * 255)
                    bitplanes[i].getRaster().setSample(x, y, 0, bit * 255);
                }
            }
            // 각 비트플레인 저장
            ImageIO.write(bitplanes[i], "jpg", new File("2_Bitplane_" + i + ".jpg"));
        }
        
        // 5. 비트플레인 중첩
        for (int i = 1; i < 8; i++) {
            BufferedImage superposed = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int accumulated = 0;
                    for (int j = 0; j <= i; j++) {
                        // 저장된 비트플레인에서 비트값(0 또는 1) 다시 추출
                        int bitValue = bitplanes[j].getRaster().getSample(x, y, 0) / 255;
                        // 가중치(2^j)를 곱해 합산 
                        accumulated += bitValue * Math.pow(2, j);
                    }
                    superposed.getRaster().setSample(x, y, 0, accumulated);
                }
            }
            // 6. 저장
            ImageIO.write(superposed, "jpg", new File("3_Superposed_0_to_" + i + ".jpg"));
        }
        System.out.println("완료되었습니다.");
	}
}