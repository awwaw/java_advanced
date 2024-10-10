package info.kgeorgiy.ja.podkorytov.i18n;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("ru", "ru"));
        System.out.println(nf.format(123));
//        final String text = """
//                Анализируемый файл "input.txt".
//                Сводная статистика
//                    Число предложений: 30.
//                    Число слов: 117.
//                    Число чисел: 37.
//                    Число сумм: 3.
//                    Число дат: 3.
//                Статистика по предложениям
//                    Число предложений: 30 (30 различных).
//                    Минимальное предложение: "Анализируемый файл "input.txt".".
//                    Максимальное предложение: "Число чисел: 37.".
//                    Минимальная длина предложения: 13 ("Число дат: 3.").
//                    Максимальная длина предложения: 109 ("GK: если сюда поставить реальное предложение, то процесс не сойдётся").
//                    Средняя длина предложения: 37,333.
//                Статистика по словам
//                    Число слов: 117 (48 различных).
//                    Минимальное слово: "GK".
//                    Максимальное слово: "языках".
//                    Минимальная длина слова: 1 ("с").
//                    Максимальная длина слова: 15 ("стабилизировать").
//                    Средняя длина слова: 6,641.
//                Статистика по числам
//                    Число чисел: 37 (22 различных).
//                    Минимальное число: -12345,67.
//                    Максимальное число: 12345,67.
//                    Среднее число: 208,847.
//                Статистика по суммам денег
//                    Число сумм: 3 (3 различных).
//                    Минимальная сумма: 123,00 ₽.
//                    Максимальная сумма: 523,00 ₽.
//                    Средняя сумма: 222,83 ₽.
//                Статистика по датам
//                    Число дат: 3 (3 различных).
//                    Минимальная дата: 17 мая 2024 г..
//                    Максимальная дата: 30 мая 2024 г..
//                    Средняя дата: 23 мая 2024 г..
//                """;
        final String text = """
                第44回阪神大賞典
                奥はマヤノトップガン
                （1996年3月9日、阪神競馬場）
                欧字表記	Narita Brian
                品種	サラブレッド
                性別	牡
                毛色	黒鹿毛
                白斑	星額刺毛鼻梁鼻白[† 1]・珠目上[† 2]
                生誕	1991年5月3日
                死没	1998年9月27日（8歳没・旧表記）
                登録日	1993年5月20日
                抹消日	1996年11月20日
                父	ブライアンズタイム
                母	パシフィカス
                母の父	Northern Dancer
                生国	日本の旗 日本（北海道新冠町）
                生産者	早田牧場新冠支場
                馬主	山路秀則
                調教師	大久保正陽（栗東）
                調教助手	大久保雅稔
                寺田雅之
                厩務員	村田光雄[1]
                競走成績
                タイトル	中央競馬クラシック三冠（1994年）
                JRA賞年度代表馬（1994年）
                最優秀3歳牡馬（1993年）
                最優秀4歳牡馬（1994年）
                顕彰馬（1997年選出）
                生涯成績	21戦12勝
                獲得賞金	10億2691万6000円
                勝ち鞍
                GI	朝日杯3歳S	1993年
                GI	皐月賞	1994年
                GI	東京優駿	1994年
                GI	菊花賞	1994年
                GI	有馬記念	1994年
                GII	スプリングS	1994年
                GII	阪神大賞典	1995年・1996年
                GIII	共同通信杯4歳S	1994年
                テンプレートを表示
                ナリタブライアン（欧字名:Narita Brian、1991年5月3日 - 1998年9月27日）は、日本の競走馬、種牡馬。
                                
                中央競馬史上5頭目のクラシック三冠馬であり、そのトレードマークから「シャドーロールの怪物」という愛称で親しまれた。1993年8月にデビューし、同年11月から1995年3月にかけてクラシック三冠を含むGI5連勝、10連続連対を達成し、1993年JRA賞最優秀3歳牡馬[† 3]、1994年JRA賞年度代表馬および最優秀4歳牡馬[† 3]に選出された。1995年春に故障（股関節炎）を発症したあとは低迷し、6戦して重賞を1勝するにとどまったが（GIは5戦して未勝利）、第44回阪神大賞典におけるマヤノトップガンとのマッチレースや短距離戦である第26回高松宮杯への出走によってファンの話題を集めた。第26回高松宮杯出走後に発症した屈腱炎が原因となって1996年10月に競走馬を引退した。引退後は種牡馬となったが、1998年9月に胃破裂を発症し、安楽死の措置がとられた。
                                
                半兄に1993年のJRA賞年度代表馬ビワハヤヒデがいる。1997年日本中央競馬会 (JRA) の顕彰馬に選出された[2]。
                                
                                
                生涯
                誕生・デビュー前
                ナリタブライアンは1991年5月3日、北海道新冠町にある早田牧場新冠支場にて誕生した。父・ブライアンズタイムは早田牧場が中心となったシンジケートが組まれてアメリカから輸入された種牡馬[3]。本馬はその初年度産駒にあたる。母・パシフィカスにとって本馬は第5仔であるが、1989年にシャルードの産駒を宿した状態でイギリス・ニューマーケットで行われたノベンバーセールに上場され、牧場経営者の早田光一郎に3万1千ギニー（約560万円）で落札され、前年に半兄・ビワハヤヒデを出産していた[4]。
                                
                早田や場長の太田三重によると、誕生後しばらくはこれといって目立つ馬ではなかったが[5][6]、次第にその身体能力が鍛錬にあたった牧場スタッフによって高く評価されるようになった。1992年10月以降、資生園早田牧場新冠支場で行われた初期調教においてナリタブライアンの調教を担当した其浦三義は、バネや背中の柔らかさ、敏捷性において半兄のビワハヤヒデをはるかに超える素質を感じたと述べている[7]。また早田によると、初期調教が行われていた時期に複数の馬に牧場内の坂を上り下りさせる運動をさせたころ、ナリタブライアンだけまったく呼吸が乱れなかったという[5]。一方で調教中に水たまりに驚いて騎乗者を振り落とすなど臆病な気性も見せた[8][† 4]。
                                
                ナリタブライアンは庭先取引によって山路秀則に購入され、中央競馬の調教師大久保正陽の厩舎で管理されることが決定した。早田によるとナリタブライアンの馬主が山路に、調教師が大久保に決定した経緯は以下の通りである。まず家畜取引商・工藤清正の仲介により大久保に紹介され、大久保が山路に購入を打診。山路と大久保が資生園早田牧場を訪れ購入が決定した。大久保はのちに「ビワハヤヒデの活躍が早ければナリタブライアンは自分のところにはやってこなかった」と述懐している[10]。取引価格は山路によれば「2,400万か2,500万」円から「100万くらい」値引きしてもらった額であったという[11]。
                                
                競走馬時代
                3歳（1993年）
                競走内容
                ナリタブライアンは1993年5月13日に日本中央競馬会 (JRA) の馬体検査を受け合格[8]。同年5月19日、栗東トレーニングセンターの大久保正陽厩舎に入厩した[12]。主戦騎手は南井克巳に決定した。その経緯について南井は、大久保に「君はダービーを勝ったことがあるか」と問われ、ないと答えたところ「じゃあうちの馬に乗ってダービーを勝ってくれないか」と持ちかけられたと述べている[13][14][15][† 5]。ただし、大久保はこうしたやり取りがあったことを否定している[13]。ナリタブライアンに初めて騎乗した南井は、次のような思いを抱いたという。
                                
                そうなんだ……何というか、跨いだ瞬間から、あっ、これ、これは、これまでの馬とは違うなって感じだった。追い切りで15-15からあとの速いキャンターにギア・チェンジするとき、グググッと重心を下げて加速してくる。体の前半分がグンと落ちて、そのあとでギューンと前へ動く。あっ、この感触、今までに一度だけ体験したことがある。そうだ。オグリキャップに追い切りで乗った時と同じだ。ウワァ、すごいって感じだった。
                — 木村2000、102頁。
                8月15日、ナリタブライアンは函館競馬場の新馬戦でデビューした。「ビワハヤヒデの弟」として注目を集め2番人気に支持されたが2着に敗れ、中1週で再び同競馬場の新馬戦に出走して初勝利を挙げた。その後、3戦目の重賞函館3歳ステークスと5戦目の重賞デイリー杯3歳ステークスではそれぞれ6着と3着に敗れたものの、4戦目のきんもくせい特別と6戦目の京都3歳ステークスを優秀な走破タイム[† 6]で優勝した。1番人気に支持されたGI朝日杯3歳ステークスでは、序盤に馬群の中ほどにつけ第3コーナーで前方へ進出を開始する走りを見せ優勝。GI初優勝を達成し、同年のJRA賞最優秀3歳牡馬に選ばれた。
                                
                気性面の問題と対策
                デビュー後まもなく、ナリタブライアンには気性面で2つの問題が現れた。1つは常にテンションが高く、特にレースが近づくとそれを察知し一層興奮する傾向があったことである[17]。この問題に対処するために、大久保はローテーションの間隔を詰めて多くのレースに出走させることによって同馬のエネルギーを発散させ興奮を和らげようとした[† 7]。ただしこの傾向は栗東トレーニングセンター内においてのみ表れた症状であり、のちにナリタブライアンが股関節炎を発症し早田牧場で休養していたときは大人しく、様子を見るために訪れた大久保が「牧場ではこんなに穏やかで優しい目をしているのか」と言ったほどであった[18]。
                                
                2つ目の問題は生来臆病な性格であったために疾走中に自分の影を怖がり、レースにおいて走りに集中することができなかったことである。この問題はシャドーロールを装着させて下方の視界を遮ることによって解決され[19]、初めてシャドーロールが装着された京都3歳ステークス以降のレースでは競馬評論家の大川慶次郎が「精神力のサラブレッド」と評するほどの優れた集中力を発揮するようになった[20]。江面弘也はナリタブライアンがシャドーロールを装着するに至った経緯について、大久保の父である亀治がかつて管理していたパッシングゴールが鼻にシャドーロールを装着してから成績が安定したことを思い出したからだと述べている[21][22]。
                                
                もっともシャドーロール装着以前からナリタブライアンの関係者は同馬の素質を高く評価しており、大久保や南井は同馬が敗れたレースにおいてもその素質を賞賛するコメントを残した（ナリタブライアンの関係者による評価を参照）。
                                
                4歳（1994年）
                競走内容
                4歳となったナリタブライアンの初戦には、東京優駿（日本ダービー）を見据え東京競馬場のコースを経験させておこうという大久保の意向により、1994年2月14日の共同通信杯4歳ステークスが選ばれた[23]。レースでは馬群の中ほどに控え、最後の直線入り口で早くも先頭に並びかけるとそのまま抜け出して優勝した。なお前日には兄のビワハヤヒデが京都記念を優勝しており、兄弟による連日の重賞制覇となった[† 8][24][25]。
                                
                野平祐二が第54回皐月賞を「大人と子供の戦い」[46]、東京優駿を「1頭だけ別次元」[33]と評したように、ナリタブライアンはクラシック三冠の序盤においてすでに同世代の競走馬を能力的に大きく凌ぐ存在として認識された。そのため1994年上半期の古馬中長距離路線において3戦3勝、GI2勝の成績を収めた兄ビワハヤヒデを最大のライバルとみなし、兄弟対決に期待するムードが高まった[47]。ビワハヤヒデの管理調教師であった浜田光正は、ナリタブライアンが皐月賞を優勝した際に本馬について「4歳春の時点での単純比較なら、すでにビワハヤヒデを超えている」と評し、「順調なら暮れの有馬記念で兄弟対決が避けられないからね」と語り[16]、ビワハヤヒデが天皇賞（春）を優勝した時点で「弟があんな強い勝ち方をするんだから兄の面目にかけても負けられない。年度代表馬の座を賭けることになるだろう」というコメントを出した[48]。ナリタブライアンが東京優駿を勝利した直後には、「兄弟対決は絶対やりたい。それまでビワは放牧に出さずしっかり作るつもりです」と兄弟対決に強い意欲を示していた[49]。一方、2頭の生産者である早田光一郎は、ナリタブライアンが皐月賞を勝った時点で「ビワハヤヒデよりも上」と評価していた[50]。また武豊はビワハヤヒデが宝塚記念で圧勝した直後に「ナリタブライアンなら、もっとすごい勝ち方をしていたはず。現時点でもナリタブライアンの方が上。あの馬の強さはケタ違い」と語っている[51][52]。
                                
                ビワハヤヒデ陣営は後半シーズン開始前にジャパンカップ不出走を表明したため、有馬記念における兄弟対決実現に期待が集まったが、ビワハヤヒデは天皇賞（秋）において発症した故障により引退を余儀なくされ、対決は実現しなかった[53]。天皇賞から一週間後に行われた菊花賞において実況を行った杉本清は、最後の直線でナリタブライアンが先頭に立つと「弟は大丈夫だ」という言葉を数回挿みながらその模様を伝えた[54]。
                                
                兄弟の比較について、野平祐二は「中距離では互角、長距離では心身両面の柔軟性に優れるナリタブライアンにやや分がある」[55]と述べている。血統評論家の久米裕は2頭について「血統構成上は甲乙つけがたい」としたうえで、1,600 - 2,000メートルではビワハヤヒデが有利、2,400メートルでは互角、3,000 - 3,200メートルではナリタブライアンが有利と述べている[56][57]。競馬評論家の大川慶次郎は有馬記念における対決が実現していた場合の結果について、「ビワハヤヒデが有馬記念に出ていたら勝っていたんじゃないか」と予想している[58]。浜田は後に「相手は三冠馬。敬意を表すどころの存在ではないのですが、ハヤヒデの安定性をもってすれば、戦っても面白かったでしょうね」と述べ[59]、ビワハヤヒデの主戦騎手であった岡部幸雄は自身の騎手引退後に出版した自著において「兄弟対決になってもブライアンをねじ伏せられた可能性も低くはなかっただろう」と述べている[60]。
                                
                5歳（1995年）
                                
                6歳（1996年）
                競走内容
                                
                第44回阪神大賞典で並走するナリタブライアン（右）とマヤノトップガン（左）
                1996年初戦には前年と同じく阪神大賞典が選択された。レースでは前年の年度代表馬マヤノトップガンをマッチレースの末に下し、同レース連覇を果たすとともに1年ぶりの勝利を挙げた。このレースについて調教師の大久保は「手に汗を握るほど興奮した」、調教助手の村田光雄は「あんなレースは初めて見た」と語り、大久保は「あのレースでは、たとえ負けていても、勝った相手を素直に祝福できたように思います」と述べている[77]。この第44回阪神大賞典はしばしば日本競馬史上の名勝負のひとつに挙げられるが[78]、その一方で名勝負とされていることを真っ向から否定する意見もある[† 12][† 13][† 14][† 15]（レースに関する詳細については第44回阪神大賞典を参照）。
                                
                11月9日には京都競馬場で、11月16日には東京競馬場で引退式が行われ、京都競馬場では菊花賞優勝時のゼッケン「4」を着け、東京競馬場では日本ダービー優勝時のゼッケン「17」を着けて引退式が行われた[88]。関東と関西2か所で引退式が行われた競走馬はシンザン、スーパークリーク、オグリキャップに続きJRA史上4頭目であった[89]。1997年には史上24頭目の顕彰馬に選出された[2]。
                                
                引退後
                種牡馬となる
                1997年に生まれ故郷である新冠町のCBスタッド（早田牧場の傘下）で種牡馬となり、内国産馬として史上最高額となる20億7,000万円のシンジケート（1株3,450万円×60株）が組まれた[90]。1997年には81頭、1998年には106頭の繁殖牝馬と交配された。交配相手にはアラホウトクやファイトガリバーといった牝馬クラシックホース、アグサン（ビワハイジの母）やモミジダンサー（マーベラスサンデーの母）など繁殖実績の高い輸入馬、スカーレットブーケといった国内外の良血繁殖牝馬が集められた。
                                
                胃破裂により死亡
                1998年6月17日、ナリタブライアンは疝痛を発症し[† 16]、三石家畜診療センターで診察を受けた結果腸閉塞を発症していることが判明した。緊急の開腹手術が行われ、いったんは快方に向かったが、9月26日午後に再び疝痛を起こした[37]。CBスタッドから50分ほど離れていた三石家畜診療センターに運び込まれた際にはすでに胃破裂を発症しており、開腹手術を行ったものの手遅れであった[37][92]。9月27日に安楽死の措置がとられた[91][92][93]。
                                
                早田光一郎によれば、ナリタブライアンは疝痛を起こした日の昼までは、ちょうどスタッドを訪れていた山路秀則と早田を前に、機嫌がよさそうな様子を見せていた[94]。夜になって突然疝痛の症状が現れたあとも、診療センターに付き添ったスタッド場長の佐々木功は「すぐに帰れる」と踏んでいたが、夜が明けても疝痛は治まらず、開腹した際に腸捻転と胃破裂が発見された[94]。佐々木は獣医師から「どうにもならない」と告げられたという[95]。ナリタブライアンは診療センターに運び込まれる直前、前脚で地面を掻き込む動作をした後に横になって自分の腹をのぞき込むような素振りを見せ[92]、佐々木は「我慢強い馬で頑張り屋だから、痛くても無理をしていたのかもしれない」と語っている[94]。なお、ナリタブライアンの馬房には監視カメラも設置されており、夜には佐々木自ら見回りも行っていた[94]。
                                
                死後
                ナリタブライアンは9月27日にCBスタッドの敷地内に埋葬された[96]。同年10月2日にはCBスタッドにて追悼式が行われ、関係者・ファンおよそ500人が参列した[97][98]。
                                
                死後、1999年9月に栗東トレーニングセンター内にナリタブライアンの馬像が建立された[99]。また、CBスタッド場長の佐々木功は、ナリタブライアンが使用していた馬房は「永久欠番」にすることを明かした[100]。命日にあたる2000年9月27日にナリタブライアン記念館が開館した（2008年9月30日閉館）[101]。中央競馬クラシック三冠達成から10年後の2004年10月、JRAゴールデンジュビリーキャンペーンの「名馬メモリアル競走」の一環として「ナリタブライアンメモリアル」が同年の菊花賞施行日に京都競馬場にて施行された（優勝馬ハットトリック）。
                
                """;
        final String text1 = "1993年5月20日 1998年6月17日";

        TextStatistics stats = new TextStatistics(Locale.of("ja", "JP"), text);
        List<Statistic<?>> allStats = stats.getAllStatistics();
        System.out.println("done");
//        System.out.println(Locale.of("ru", "ru").getCountry());
//        System.out.println(Locale.of("ru", "ru").getLanguage());

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        df.setCalendar(Calendar.getInstance(Locale.of("ja", "JP")));
        System.out.println(df.format(new Date("1970-01-01")));
//
        TextStatistics.main(new String[]{"ja-JP", "ru-RU",
                "C:\\Users\\aww\\Desktop\\adv\\java-advanced\\java-solutions\\info\\kgeorgiy\\ja\\podkorytov\\i18n\\input.txt",
                "C:\\Users\\aww\\Desktop\\adv\\java-advanced\\java-solutions\\info\\kgeorgiy\\ja\\podkorytov\\i18n\\output.txt"});
    }
}