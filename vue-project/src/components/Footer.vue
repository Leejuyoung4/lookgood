<template>
  <div class="footer-container">
    <RouterLink to="/" class="logo" @click="scrollToTop">
    <div class="logo-container">
         <span class="logo">
           <span class="logo-item">L</span>
           <span class="logo-item logo-bold">oo</span>
           <span class="logo-item">k</span>
           <span class="logo-item">G</span>
           <span class="logo-item logo-bold">oo</span>
           <span class="logo-item">d</span>
          </span>
        </div>
      </RouterLink>

    <!-- Social Media Icons -->
    <div class="social-icons">
      <a href="https://www.instagram.com/">
        <img class="icon" :src="instagram0" alt="Instagram" />
      </a>
      <a href="https://www.facebook.com/">
        <img class="icon" :src="facebook0" alt="Facebook" />
      </a>
      <a href="https://www.youtube.com/">
        <img class="icon" :src="youtube0" alt="YouTube" />
      </a>
      <a @click="copyLink">
        <img class="icon" :src="share20" alt="Share" />
      </a>
      <div v-if="isCopied" class="copy-alert">
        링크가 복사되었습니다!
      </div>

    </div>

    <!-- Bottom Menu -->
    <div class="bottom-menu">
      <div @click="openModal('company')">회사소개</div>
      <div @click="openModal('terms')">이용약관</div>
      <div class="highlight" @click="openModal('privacy')">개인정보처리방침</div>
      <div @click="openModal('guide')">이용안내</div>
    </div>

    <!-- Modal -->
    <Modal :isOpen="isModalOpen" @close="closeModal">
      <!-- 회사소개 -->
      <div v-if="modalContent === 'company'" class="policy-section">
        <h1 class="policy-title">회사소개</h1>
        <div class="policy-content">
          <section>
            <h2>운동과 학습의 만남, 새로운 도전의 시작</h2>
            <p>[LookGood]은 운동과 학습의 융합을 통해 더 건강하고 유익한 라이프스타일을 제안합니다. 저희는 사용자들이 원하는 목표를 달성할 수 있도록 최적화된 플랫폼과 다양한 교육 서비스를 제공합니다. 건강한 삶을 지향하며, 혁신적인 기술을 활용해 개인의 발전을 돕는 것이 우리의 목표입니다.</p>
          </section>
          
          <section>
            <h2>운동과 교육의 허브</h2>
            <ul>
              <li>다양한 스포츠 종목별 전문 강사와의 개인 맞춤형 교육 과정</li>
              <li>운동 영상을 저장하고 복습할 수 있는 '내 학습 공간' 기능</li>
              <li>사용자가 원하는 운동 코스를 쉽게 찾고 등록할 수 있는 직관적인 네비게이션</li>
            </ul>
          </section>
          <!-- 기존 내용 유지 -->
        </div>
      </div>

      <!-- 이용약관 -->
      <div v-if="modalContent === 'terms'" class="policy-section">
        <h1 class="policy-title">이용약관</h1>
        <div class="policy-content">
          <section>
            <h2>[제 1 장 총칙]</h2>
            <p>본 약관은 본 홈페이지를 이용하여 온라인 운동 교육 플랫폼을 주관하는 회사 (LookGood)가 운영하는 서비스와 관련하여 회사와 이용자와의 권리/의무 및 책임사항을 규정함을 목적으로 합니다.</p>
          </section>
          
          <section>
            <h2>제 2 조 용어의 정의</h2>
            <ul>
              <li>"서비스"란 접속 가능한 유/무선 정보통신기기의 종류와는 상관없이 이용 가능한 "회사"가 제공하는 모든 서비스를 의미합니다.</li>
              <li>"홈페이지"란 회사가 이용자에게 서비스를 제공하기 위하여 컴퓨터 등 정보통신설비를 이용하여 구성한 가상의 공간을 의미하며, "홈페이지" URL은 http://172.30.1.91:5173/ 입니다. 단, 학습에 필요한 세부 사이트(project.ssafy.com/jira.ssafy.com, cert.ssafy.com 등) 이용 권한은 회사가 정한 기준(해당 학기별 학습자 등)에 따라 일부 "회원"에게만 부여합니다.</li>
              <li>"회원"이란 회사에 개인정보를 제공하여 회원등록을 한 자로서, 회사와 서비스 이용계약을 체결하고 회원 아이디를 부여 받은 자를 의미합니다.</li>
              <li>"회원 아이디(이하 "ID"라 한다)"란 회원의 식별과 회원의 서비스 이용을 위하여 회원이 선정하고 회사가 검증 및 승인하는 코드로, 회원의 이메일 주소를 사용합니다.</li>
              <li>"비밀번호"란 회원이 부여 받은 ID(회원의 이메일 주소)와 일치된 회원임을 확인하고, 회원의 개인정보를 보호하기 위하여 회원이 정한 문자와 숫자의 조합을 의미합니다.</li>
              <li>'회원등록 해지(이하 "해지")'란 회사 또는 회원이 이용계약을 해지하는 것을 의미합니다.</li>
              <li>본 약관에서 사용하는 용어의 정의는 본 조에서 정하는 것을 제외하고는 관계법령 및 서비스 별 안내에서 정하는 바에 따릅니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 3 조 이용약관의 효력 및 변경</h2>
            <ul>
              <li>1. 회사는 본 약관의 내용을 이용자가 쉽게 알 수 있도록 각 "홈페이지" 서비스의 초기 화면에 게시합니다.</li>
              <li>2. 본 약관에서 안내하고 있는 개인정보보호에 관한 정책은 회원가입 후 홈페이지의 정보를 활용하는 모든 경우에 해당됩니다.</li>
              <li>3. 회사는 약관의 규제에 관한 법률, 전자거래기본법, 전자 서명법, 정보통신망이용 촉진 및 정보보호 등에 관한 법률 등 관련법을 위배하지 않는 범위에서 본 약관을 개정할 수 있습니다. 회사는 본 약관을 개정할 경우에는 적용일자 및 개정사유를 명시하여 현행 약관과 함께 회사가 제공하는 "홈페이지"의 초기 화면에 그 적용일자 7일 이전부터 적용일자 전일까지 공지합니다. 다만, 회원에게 불리하게 약관내용을 변경하는 경우에는 최소한 30일 이상의 사전 유예기간을 두고 공지합니다. 이 경우 회사는 개정 전 내용과 개정 후 내용을 명확하게 비교하여 회원이 알기 쉽도록 표시합니다.</li>
              <li>4. 회원은 개정된 약관에 대해 거부할 권리가 있습니다. 회원은 개정된 약관에 동의하지 않을 경우 서비스 이용을 중단하고 회원등록을 해지할 수 있습니다. 단, 개정된 약관의 효력 발생일 이후에도 서비스를 계속 이용할 경우에는 약관의 변경사항에 동의한 것으로 간주합니다.</li>
              <li>5. 변경된 약관에 대한 정보를 알지 못해 발생하는 회원의 피해는 회사가 책임지지 않습니다.</li>
              <li>6. 변경된 약관에 대한 정보를 알지 못해 발생하는 회원의 피해는 회사가 책임지지 않습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 4 조 약관의 해석</h2>
            <ul>
              <li>1. 본 약관은 회사가 제공하는 각 서비스 별 운영규칙과 함께 적용됩니다.</li>
              <li>2. 본 약관에 명시되지 아니한 사항과 본 약관의 해석에 관하여는 관계법령 또는 상관례에 따릅니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 2 장 이용계약 체결]</h2>
            <ul>
              <li>제 5 조 이용계약의 성립</li>
              <li>1. 이용계약은 회원의 본 이용약관 내용에 대한 동의와 이용신청에 대하여 회사의 이용승낙으로 성립합니다.</li>
              <li>2. 본 이용약관에 대한 동의는 회원등록 당시 본 약관을 읽고 "위 서비스 약관에 동의합니다"의 대화창에 표시를 한 후 등록하기 단추를 누름으로써 의사표시를 한 것으로 간주합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 6 조 서비스 이용신청</h2>
            <ul>
              <li>1. 회원으로 등록하여 본 서비스를 이용하고자 하는 이용고객은 회사에서 요청하는 제반정보(이름, 이메일 주소, 비밀번호 등)를 제공하여야 합니다.</li>
              <li>2. 회원은 반드시 회사가 제공하는 서비스가 요구하는 회원 본인인증 절차를 거쳐야만 서비스를 이용할 수 있으며, 본인인증을 거치지 않은 회원은 일체의 권리를 주장할 수 없습니다.</li>
              <li>3. 타인의 ID(이메일 및 비밀번호)를 도용하여 서비스를 이용할 경우, 관계법령에 따라 처벌을 받을 수 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 7 조 개인정보의 보호 및 사용</h2>
            <ul>
              <li>1. 회사는 관계법령이 정하는 바에 따라 회원등록정보를 포함한 회원의 개인정보를 보호하기 위해 노력합니다. 회원 개인정보의 보호 및 사용에 대해서는 관련법령 및 회사의 개인정보보호정책이 적용됩니다. 단, 회사의 공식사이트 이외의 웹에서 링크된 사이트에서는 회사의 개인정보보호정책이 적용되지 않습니다. 또한 회사는 회원의 귀책사유로 인해 노출된 정보에 대해서 일체의 책임을 지지 않습니다.</li>
              <li>2. 회사는 이용자에게 제공하는 서비스의 양적, 질적 향상을 위하여 이용자의 개인정보를 제휴사에게 제공, 공유할 수 있으며, 이 때에는 반드시 이용자의 동의를 받아 필요한 최소한의 정보를 제공, 공유하며 누구에게 어떤 목적으로 어떤 정보를 제공, 공유하는지 명시합니다.</li>
              <li>3. 회원은 원하는 경우 언제든 회사에 제공한 개인정보의 수집과 이용에 대한 동의를 철회할 수 있으며, 동의의 철회는 회원 탈퇴를 하는 것으로 이루어집니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 8 조 이용신청의 승낙과 제한</h2>
            <ul>
              <li>1. 회사는 제 6조의 규정에 의한 이용신청고객에 대하여 업무 수행상 또는 기술상 지장이 없는 경우에 원칙적으로 접수순서에 따라 서비스 이용을 승낙합니다.</li>
              <li>2. 회사는 다음 각 호의 내용에 해당하는 경우 이용신청에 대한 승낙을 제한할 수 있고, 그 사유가 해소될 때까지 승낙을 유보할 수 있습니다.</li>
              <li>⑴ 회사의 서비스 관련설비에 여유가 없는 경우</li>
              <li>⑵ 회사의 기술상 지장이 있는 경우</li>
              <li>⑶ 기타 회사의 사정상 필요하다고 인정되는 경우</li>
              <li>3. 회사는 다음 각 호의 내용에 해당하는 경우 이용신청에 대한 승낙을 하지 아니할 수도 있습니다.</li>
              <li>⑴ 타인의 ID를 이용하여 신청한 경우</li>
              <li>⑵ 이용계약 신청서의 내용을 허위로 기재한 경우</li>
              <li>⑶ 사회의 안녕과 질서, 미풍양속을 저해할 목적으로 신청한 경우</li>
              <li>⑷ 부정한 용도로 본 서비스를 이용하고자 하는 경우</li>
              <li>⑸ 영리를 추구할 목적으로 본 서비스를 이용하고자 하는 경우</li>
              <li>⑹ 기타 회사가 정한 등록신청 요건이 미비된 경우</li>
              <li>⑺ 본 서비스와 경쟁관계가 있는 이용자가 신청하는 경우</li>
              <li>⑻ 기타 규정한 제반사항을 위반하며 신청하는 경우</li>
              <li>4. 회사는 이용신청고객이 관계법령에서 규정하는 미성년자일 경우에 서비스 별 안내에서 정하는 바에 따라 승낙을 보류할 수 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 9 조 회원 아이디 부여 및 변경 등</h2>
            <ul>
              <li>1. 회사는 회원에 대하여 본 약관에 정하는 바에 따라 회원 ID를 부여합니다. 회원 ID는 회원 본인이 사용하는 이메일 주소를 사용하며, 회원 ID는 원칙적으로 변경이 불가합니다. 부득이한 사유로 회원 ID를 변경하고자 하는 경우에는 해당 ID를 해지하고 재가입해야 합니다.</li>
              <li>2. 회원은 서비스 페이지 또는 기타 해당 페이지로 링크된 메뉴를 통하여 자신의 개인정보를 관리할 수 있는 페이지를 열람할 수 있으며, 해당 페이지에서 언제든지 본인의 개인정보를 열람하고 수정할 수 있습니다.</li>
              <li>3. 회원 ID는 다음 각 호에 해당하는 경우에는 회원 또는 회사의 요청으로 변경할 수 있습니다.</li>
              <li>⑴ 회원 ID가 회원의 전화번호 또는 생년월일 등으로 등록되어 사생활 침해가 우려되는 경우</li>
              <li>⑵ 타인에게 혐오감을 주거나 미풍양속에 어긋나는 경우</li>
              <li>⑶ 기타 합리적인 사유가 있는 경우</li>
              <li>4. 회원 ID 및 비밀번호의 관리책임은 회원에게 있습니다. 이를 소홀이 관리하여 발생하는 서비스 이용상 손해 또는 제3자에 의한 부정이용 등에 대한 책임은 회원에게 있으며 회사는 그에 대한 책임을 일절 지지 않습니다.</li>
              <li>5. 기타 회원 개인정보 관리 및 변경 등에 관한 사항은 서비스 별 안내에 정하는 바에 의합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 10 조 (회원탈퇴 및 자격 상실 등)</h2>
            <ul>
              <li>"회원"은 "회사"에 언제든지 탈퇴를 요청할 수 있으며 "회사"는 즉시 회원탈퇴를 처리합니다. 단 탈퇴한 회원은 탈퇴일로부터 7일 동안은 동일한 아이디로 회원가입을 할 수 없습니다.</li>
              <li>"회원"이 다음 각호의 사유에 해당하는 경우, "회사"는 회원자격을 제한 및 정지시킬 수 있습니다.</li>
              <li>① 가입신청 시에 허위내용을 등록한 경우</li>
              <li>② 다른 사람의 "회사"의 서비스이용을 방해하거나 그 정보를 도용하는 경우</li>
              <li>③ "회사"를 이용하여 법령 또는 본 약관이 금지하거나 공서양속에 반하는 행위를 하는 경우</li>
              <li>3. "회사"가 회원자격을 제한/정지시킨 후, 동일한 행위가 2회 이상 반복되거나 30일 이내에 그 사유가 시정되지 아니하는 경우 "회사"는 회원자격을 상실시킬 수 있습니다.</li>
              <li>4. "회사"가 회원자격을 상실시키는 경우에는 회원등록을 말소합니다. 이 경우 "회원"에게 이를 통지하고, 회원등록 말소 전에 최소한 30일 이상의 기간을 정하여 소명할 기회를 부여합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 3 장 계약 당사자의 의무]</h2>
            <ul>
              <li>제 11 조 회사의 의무</li>
              <li>1. 회사는 관련법과 본 약관이 금지하거나 공서양속에 반하는 행위를 하지 않으며, 본 약관이 정하는 바에 따라 지속적���고, 안정적으로 서비스를 제공하기 위하여 최선을 다하여야 합니다.</li>
              <li>2. 회사는 회원이 안전하게 서비스를 이용할 수 있도록 회원의 개인정보보호를 위한 보안시스템을 구축하며 개인정보 보호정책을 공시하고 준수합니다.</li>
              <li>3. 회사는 수신거절의 의사를 명백히 표시한 회원에 대해서는 회원이 원하지 않는 영리목적의 광고성 전자우편(이메일)을 발송하지 않습니다.</li>
              <li>4. 회사는 이용계약의 체결, 계약사항의 변경 및 해지 등 회원과의 계약관계 절차 및 내용 등에 있어 회원에게 편의를 제공하도록 노력합니다. 회원이 탈퇴신청을 할 경우 즉시 탈퇴 처리를 합니다.</li>
              <li>5. 회사는 이용고객으로부터 제기되는 의견이나 불만이 정당하다고 객관적으로 인정될 경우에는 적절한 절차를 거쳐 즉시 처리하여야 합니다. 다만, 즉시 처리가 곤란한 경우는 이용자에게 그 사유와 처리일정을 통보하여야 합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 12 조 회원의 ID 및 비밀번호에 대한 의무</h2>
            <ul>
              <li>1. 회사가 관계법령 및 개인정보보호정책에 의하여 그 책임을 지는 경우를 제외하고, 회원 ID와 비밀번호의 관리책임은 회원에게 있습니다. 회원은 자신의 ID 및 비밀번호를 제3자가 이용하게 해서는 안됩니다.</li>
              <li>2. 회원이 자신의 ID 및 비밀번호를 도용 당하거나 제3자가 사용하고 있음을 인지한 경우에는 바로 회사에 통보하고 회사의 안내가 있는 경우에는 그에 따라야 합니다.</li>
              <li>3. 회사는 회원이 상기 제1항, 제2항, 제3항을 위반하여 회원에게 발생한 손해에 대하여 책임을 일절 지지 않습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 13 조 회원의 의무</h2>
            <ul>
              <li>1. 회원은 본 약관에 규정하는 사항과 기타 회사가 정한 제반 규정, 서비스 이용안내 또는 공지사항 등 회사가 공지 또는 통지하는 사항 및 관계법령을 준수하여야 하며, 기타 회사의 업무에 방해가 되는 행위, 회사의 명예를 손상 시키는 행위를 하여서는 안됩니다.</li>
              <li>2. 회사가 관계법령 및 개인정보보호정책에 의하여 그 책임을 지는 경우를 제외하고 제11조의 관리소홀, 부정사용에 의하여 발생되는 모든 결과에 대한 책임은 회원에게 있습니다.</li>
              <li>3. 회원은 회사의 명시적인 동의가 없는 한 서비스의 이용권한, 기타 이용계약 상의 지위를 타인에게 양도, 증여할 수 없으며, 이를 담보로 제공할 수 없습니다.</li>
              <li>4. 회원은 서비스 이용과 관련하여 제31조 제1항의 각 호에 해당하는 행위를 하여서는 안됩니다.</li>
              <li>5. 이용자는 회원가입 신청 또는 회원정보 변경 시 실명으로 모든 사항을 사실에 근거하여 작성하여야 하며, 허위 또는 타인의 정보를 등록할 경우 일체의 권리를 주장할 수 없습니다.</li>
              <li>6. 회원은 이메일 주소 등 이용계약사항이 변경된 경우에 해당 절차를 거쳐 이를 회사에 즉시 알려야 합니다.</li>
              <li>7. 회원은 회사 및 제3자의 지적 재산권을 침해해서는 안됩니다.</li>
              <li>8. 회원은 다음 각 호에 해당하는 행위를 하여서는 안되며, 해당 행위를 하는 경우에 회사는 회원의 서비스 이용제한 및 적법조치를 포함한 제재를 가할 수 있습니다.</li>
              <li>⑴ 회원가입 신청 또는 회원정보 변경 시 허위내용을 등록하는 행위</li>
              <li>⑵ 다른 이용자의 ID, 비밀번호, 이메일 주소를 도용하는 행위</li>
              <li>⑶ 이용자 ID를 타인과 거래하는 행위</li>
              <li>⑷ 회사의 운영진, 직원 또는 관계자를 사칭하는 행위</li>
              <li>⑸ 회사로부터 특별한 권리를 부여받지 않고 회사의 클라이언트 프로그램을 변경하거나, 회사의 서버를 해킹하거나, 웹사이트 또는 게시된 정보의 일부분 또는 전체를 임의로 변경하는 행위</li>
              <li>⑹ 서비스에 위해를 가하거나 고의로 방해하는 행위</li>
              <li>⑺ 본 서비스를 통해 얻은 정보를 회사의 사전 승낙 없이 서비스 이용 외의 목적으로 복제하거나, 이를 출판 및 방송 등에 사용하거나, 제 3자에게 제공하는 행위</li>
              <li>⑻ 회사 또는 제3자의 저작권 등 기타 지적재산권을 침해하는 내용을 전송, 게시, 전자우편 또는 기타의 방법으로 타인에게 유포하는 경우</li>
              <li>⑼ 공공질서 및 미풍양속에 위반되는 저속, 음란한 내용의 정보, 문장, 도형, 음향, 동영상을 전송, 게시, 전자우편 또는 기타의 방법으로 타인에게 유포하는 행위</li>
              <li>⑽ 모욕적이거나 개인신상에 대한 내용이어서 타인의 명예나 프라이버시를 침해할 수 있는 내용을 전송, 게시, 전자우편 또는 기타의 방법으로 타인에게 유포하는 행위</li>
              <li>⑾ 다른 이용자를 희롱 또는 위협하거나, 특정 이용자에게 지속적으로 고통 또는 불편을 주는 행위</li>
              <li>⑿ 회사의 승인을 받지 않고 다른 사용자의 개인정보를 수집 또는 저장하는 행위</li>
              <li>⒀ 범죄와 결부된다고 객관적으로 판단되는 행위</li>
              <li>⒁ 본 약관을 포함하여 기타 회사가 정한 제반 규정 또는 이용 조건을 위반하는 행위</li>
              <li>⒂ 기타 관계법령에 위배되는 행위</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 4 장 서비스 이용]</h2>
            <ul>
              <li>제 14 조 정보의 제공 및 통지</li>
              <li>1. 회사는 회원이 서비스 이용 중 필요하다고 인정되는 다양한 정보를 공지사항 혹은 전자우편 등의 방법으로 회원에게 제공할 수 있습니다.</li>
              <li>2. 회사는 불특정다수 회원에 대한 통지를 하는 경우 공지 게시판에 게시함으로써 개별 통지에 갈음할 수 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 15 조 게시물의 저작권 및 관리</h2>
            <ul>
              <li>1. 회사는 회원의 게시물을 소중하게 생각하며 변조, 훼손, 삭제되지 않도록 최선을 다하여 보호합니다. 다만, 다음 각 호에 해당하는 게시물이나 자료의 경우 사전통지 없이 삭제하거나 이동 또는 등록 거부를 할 수 있으며, 해당 회원의 자격을 제한, 정지 또는 상실시킬 수 있습니다.</li>
              <li>⑴ 다른 회원 또는 제 3자에게 심한 모욕을 주거나 명예를 손상시키는 내용인 경우</li>
              <li>⑵ 공공질서 및 미풍양속에 위반되는 내용을 유포하거나 링크시키는 경우</li>
              <li>⑶ 불법복제 또는 해킹을 조장하는 내용인 경우</li>
              <li>⑷ 영리를 목적으로 하는 광고일 경우</li>
              <li>⑸ 범죄적 행위에 결부된다고 인정되는 내용인 경우</li>
              <li>⑹ 회사나 다른 회원의 저작권 혹은 제3자의 저작권 등 기타 권리를 침해하는 내용인 경우</li>
              <li>⑺ 회사에서 규정한 게시물 원칙에 어긋나거나, 게시판 성격에 부합하지 않는 경우</li>
              <li>⑻ 스팸성 게시물인 경우</li>
              <li>⑼ 기타 관계법령에 위배된다고 판단되는 경우</li>
              <li>2. 회사가 작성한 저작물에 대한 저작권 및 기타 지적재산권은 회사에 귀속됩니다. 회원이 서비스 화면 내에 게시한 게시물의 저작권은 게시한 회원에게 귀속됩니다. 또한 회사는 게시자의 동의 없이 게시물을 상업적으로 이용할 수 없습니다. 다만, 입사지원서의 경우 관련법규 및 회사규정에 따라 제출된 지원서를 회원 본인에게 반환하지 않을 수 있습니다.</li>
              <li>3. 회원은 서비스를 이용하여 취득한 정보를 회사의 사전 승낙 없이 임의가공, 판매, 복제, 송신, 출판, 배포, 방송 기타 방법에 의하여 영리목적으로 이용하거나 제3자에게 이용하게 하여서는 안됩니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 16 조 광고게재 및 광고주와의 거래</h2>
            <ul>
              <li>1. 회사가 제공하는 서비스 내에 다양한 배너와 링크(Link)를 포함할 수 있으며, 이는 광고주와의 계약관계에 의하거나 제공받은 컨텐츠의 출처를 밝히기 위한 조치입니다. 회원은 서비스 이용 시 노출되는 광고게재에 대해 동의합니다.</li>
              <li>2. 서비스 내에 포함되어 있는 링크를 클릭하여 타 사이트의 페이지로 옮겨갈 경우 해당 사이트의 개인정보보호정책은 회사와 무관합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 17 조 서비스 제공의 중단 등</h2>
            <ul>
              <li>1. 회사는 다음 각 호의 내용에 해당하는 경우 서비스 제공의 일부 혹은 전부를 제한하거나 중단할 수 있습니다.</li>
              <li>⑴ 정보통신설비의 보수 점검, 교체 및 고장 등 공사로 인한 부득이 한 경우</li>
              <li>⑵ 기간통신사업자가 전기통신서비스를 중단했을 경우</li>
              <li>⑶ 서비스 이용의 폭주 등으로 정상적인 서비스 이용에 지장이 있는 경우</li>
              <li>⑷ 국가비상사태 등 기타 불가항력적인 사유가 있는 경우</li>
              <li>2. 전항에 의한 서비스 중단의 경우에는 회사는 제14조에 정한 방법으로 그 사유 및 기간 등을 공지합니다. 다만, 회사가 통제할 수 없는 사유로 발생한 서비스의 중단(시스템관리자의 고의, 과실 없는 디스크 장애, 시스템 다운 등)에 대하여 사전공지가 불가능한 경우에는 예외로 합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 18 조 회원 ID 관리</h2>
            <ul>
              <li>1. 회원 ID와 비밀번호에 관한 모든 관리책임은 회원에게 있습니다. 회원이 등록한 회원 ID 및 비밀번호에 의하여 발생되는 사용상의 과실 또는 제3자에 의한 부정사용 등에 대한 모든 책임은 해당 이용고객에게 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 19 조 정보의 제공</h2>
            <ul>
              <li>1. 회사는 회원에게 서비스 이용에 필요가 있다고 인정되는 각종 정보에 대해서 전자우편이나 서신우편 등의 방법으로 회원에게 제공할 수 있습니다. 회사는 서비스 개선 및 회원 대상의 서비스 소개 등의 목적으로 회원의 동의 하에 추가적인 개인 정보를 요구할 수 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 5 장 컨텐츠 서비스 제공]</h2>
            <ul>
              <li>제 20 조 ("콘텐츠"의 내용 등의 게시)</li>
              <li>1. "회사"는 "콘텐츠"의 내용, 이용방법 등을 이용초기화면에 "이용자"가 알기 쉽게 표시합니다. 2. "회사"는 "콘텐츠"별 이용가능기기 및 이용에 필요한 최소한의 기술사양에 관한 정보를 "이용자"에게 제공합니다.</li>
              <li>제 21 조 ("콘텐츠" 이용 신청)</li>
              <li>1. "이용자"는 "회사"가 제공하는 다음 또는 이와 유사한 절차에 의하여 이용신청을 합니다. ① "콘텐츠" 목록의 열람 및 선택 ② "콘텐츠"의 이용신청에 관한 확인 또는 "회사"의 확인에 대한 동의 2. "회사"는 "이용자"의 이용신청이 다음 각 호에 해당하는 경우에는 승낙하지 않거나 승낙을 유보할 수 있습니다. ① 실명이 아니거나 타인의 명의를 이용한 경우 ② 허위의 정보를 기재하거나, "회사"가 제시하는 내용을 기재하지 않은 경우 ③ 서비스 관련 설비의 여유가 없거나, 기술상 또는 업무상 문제가 있는 경우 3. "회사"의 승낙의 의사표시에는 "이용자"의 이용신청에 대한 확인 및 서비스제공 가능여부, 이용신청의 정정/취소 등에 관한 정보 등을 포함합니다.</li>
              <li>제 22 조 ("회사"의 의무)</li>
              <li>1. "회사"는 법령과 본 약관이 정하는 권리의 행사와 의무의 이행을 신의에 좇아 성실하게 하여야 합니다. 2. "회사"는 "이용자"가 안전하게 "콘텐츠"를 이용할 수 있도록 개인정보(신용정보 포함)보호를 위해 보안시스템을 갖추어야 하며 개인정보보호정책을 공시하고 준수합니다. 3. "회사"는 콘텐츠 이용과 관련하여 "이용자"로 부터 제기된 의견이나 불만이 정당하다고 인정할 경우에는 이를 지체없이 처리합니다. 이용자가 제기한 의견이나 불만사항에 대해서는 게시판을 활용하거나 전자우편 등을 통하여 그 처리과정 및 결과를 전달합니다.</li>
              <li>제 23 조 ("이용자"의 의무)</li>
              <li>1. "이용자"는 다음 행위를 하여서는 안 됩니다. ① 신청 또는 변경 시 허위내용의 기재 ② 타인의 정보도용 ③ "회사"에 게시된 정보의 변경 ④ "회사"가 금지한 정보(컴퓨터 프로그램 등)의 송신 또는 게시 ⑤ "회사"와 기타 제3자의 저작권 등 지식재산권에 대한 침해 ⑥ "회사" 및 기타 제3자의 명예를 손상시키거나 업무를 방해하는 행위 ⑦ 외설 또는 폭력적인 말이나 글, 화상, 음향, 기타 공서양속에 반하는 정보를 "회사"의 사이트에 공개 또는 게시하는 행위 ⑧ 기타 불법적이거나 부당한 행위 2. "이용자"는 관계법령, 본 약관의 규정, 이용안내 및 "콘텐츠"와 관련하여 공지한 주의사항, "회사"가 통지하는 사항 등을 준수하여야 하며, 기타 "회사"의 업무에 방해되는 행위를 하여서는 안 됩니다.</li>
              <li>3. 이용자는 서비스의 이용권한, 기타 이용계약 상의 지위를 타인에게 양도, 증여할 수 없으며, 이를 담보로 제공할 수 없습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 24 조 (콘텐츠 서비스의 제공 및 중단)</h2>
            <ul>
              <li>1. 콘텐츠 서비스는 연중무휴, 1일 24시간 제공함을 원칙으로 합니다. 2. "회사"는 컴퓨터 등 정보통신설비의 보수점검, 교체 및 고장, 통신두절 또는 운영상 상당한 이유가 있는 경우 콘텐츠서비스의 제공을 일시적으로 중단할 수 있습니다. 3. "회사"는 콘텐츠서비스의 제공에 필요한 경우 정기점검을 실시할 수 있으며, 정기점검시간은 서비스제공화면에 공지한 바에 따릅니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 25 조 (콘텐츠 서비스의 변경)</h2>
            <ul>
              <li>1. "회사"는 상당한 이유가 있는 경우에 운영상, 기술상의 필요에 따라 제공하고 있는 콘텐츠 서비스를 변경할 수 있습니다. 2. "회사"는 콘텐츠 서비스의 내용, 이용방법, 이용시간을 변경할 경우에 변경사유, 변경될 콘텐츠 서비스의 내용 및 제공일자 등을 그 변경 전 7일 이상 해당 콘텐츠 초기화면에 게시합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 26 조 (정보의 제공 및 광고의 게재)</h2>
            <ul>
              <li>1. "회사"는 "이용자"가 콘텐츠 이용 중 필요하다고 인정되는 다양한 정보를 등의 방법으로 "회원"에게 제공할 수 있습니다. 2. 제1항의 정보를 전자적 전송매체에 의하여 전송하려고 하는 경우에는 "회원"의 사전 동의를 받아서 전송합니다. 3. "회사"는 "콘텐츠"서비스 제공과 관련하여 콘텐츠화면, 홈페이지, 전자우편 등에 광고를 게재할 수 있습니다. 광고가 게재된 전자우편 등을 수신한 "회원"은 수신거절을 "회사"에게 할 수 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 27 조 (게시물의 삭제)</h2>
            <ul>
              <li>1. "회사"는 게시판에 정보통신망이용촉진 및 정보보호 등에 관한 법률을 위반한 매체물이 게시되어 있는 경우에는 이를 지체 없이 삭제 합니다. 2. "회사"가 운영하는 게시판 등에 게시된 정보로 인하여 법률상 이익이 침해된 자는 "회사"에게 당해 정보의 삭제 또는 반박내용의 게재를 요청할 수 있습니다. 이 경우 "회사"는 지체 없이 필요한 조치를 취하고 이를 즉시 신청인에게 통지합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 28 조 (저작권 등의 귀속)</h2>
            <ul>
              <li>1. "회사"가 작성한 저작물에 대한 저작권 기타 지식재산권은 "회사"에 귀속합니다. 2. "회사"가 제공하는 서비스 중 제휴계약에 의해 제공되는 저작물에 대한 저작권 기타 지식재산권은 해당 제공업체에 귀속합니다. 3. "이용자"는 "회사"가 제공하는 서비스를 이용함으로써 얻은 정보 중 "회사" 또는 제공업체에 지식재산권이 귀속된 정보를 "회사" 또는 제공업체의 사전서면승인 없이 복제, 전송, 출판, 배포, 방송 기타 방법에 의하여 영리목적으로 이용하거나 제3자에게 이용하게 하여서는 안 됩니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>제 29 조 (개인정보보호)</h2>
            <ul>
              <li>"회사"는 정보통신망이용촉진 및 정보보호에 관한 법률 등 관계 법령이 정하는 바에 따라 "이용자"의 "개인정보"를 보호하기 위해 노력합니다. "개인정보"의 보호 및 사용에 대해서는 관련법령 및 "회사"의 개인정보보호정책이 적용됩니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 6 장 계약해지 및 이용제한]</h2>
            <ul>
              <li>제 30 조 계약 변경 및 해지</li>
              <li>회원이 이용계약을 해지하고자 하는 경우에는 회원 본인이 사이트 내의 개인정보관리 페이지를 통해 등록 해지 신청을 하여야 합니다.</li>
              <li>제 31 조 서비스 이용제한</li>
              <li>1. 회원은 본 약관 제11조, 제12조 내용을 위반하거나 다음 각 호에 해당하는 행위를 하는 경우에 회사는 회원의 서비스 이용을 제한할 수 있습니다. ⑴ 미풍양속을 저해하는 비속한 ID 및 별명 사용 ⑵ 타 이용자에게 심한 모욕을 주거나, 서비스 이용을 방해한 경우 ⑶ 정보통신 윤리위원회 등 관련 공공기관의 시정 요구가 있는 경우 ⑷ 36개월 이상 서비스를 이용한 적이 없는 경우 ⑸ 불법 게시물을 게재한 경우 - 상용소프트웨어나 크랙파일을 올린 경우 - 정보통신윤리위원회 심의세칙 제 7조에 어긋나는 음란물을 게재한 경우 - 반국가적 행위의 수행을 목적으로 하는 내용을 포함한 경우 - 저작권이 있는 글이나 음원, 영상 등을 무단 복제하여 게재한 경우 ⑹ 기타 정상적인 서비스 운영에 방해가 될 경우 2. 상기 이용제한 규정에 따라 서비스를 이용하는 회원에게 서비스 이용에 대하여 별도 공지없이 서비스 이용의 일시정지, 초기화, 이용계약 해지 등의 조치를 취할 수 있으며, 회원은 전항의 귀책사유로 인하여 회사나 다른 회원에게 입힌 손해를 배상할 책임이 있습니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[제 7 장 기타사항]</h2>
            <ul>
              <li>제 32 조 손해배상</li>
              <li>회사는 무료로 제공하는 서비스의 이용과 관련하여 개인정보보호정책에서 정하는 내용에 해당하지 않는 사항에 대하여는 어떠한 손해도 책임을 지지 않습니다.</li>
              <li>제 33 조 (면책조항)</li>
              <li>1. 회사는 천재지변, 전쟁 및 기타 불가항력, 회사의 합리적인 통제범위를 벗어난 사유로 인하여 서비스를 제공할 수 없는 경우에는 그에 대한 책임이 면제됩니다. 2. 회사는 기간통신 사업자가 전기통신 서비스를 중지하거나 정상적으로 제공하지 아니하여 손해가 발생한 경우 책임이 면제됩니다. 3. 회사는 서비스용 설비의 보수, 교체, 정기점검, 공사 등 부득이한 사유로 발생한 손해에 대한 책임이 면제됩니다. 4. 회사는 이용자의 귀책사유로 인한 서비스 이용의 장애 또는 손해에 대하여 책임을 지지 않습니다. 5. 회사는 이용자의 컴퓨터 오류에 의해 손해가 발생한 경우, 또는 회원이 신상정보 및 전자우편 주소를 부실하게 기재하여 손해가 발생한 경우 책임을 지지 않습니다. 6. 회사는 회원이 서비스를 이용하여 기대하는 수익을 얻지 못하거나 상실한 것에 대하여 책임을 지지 않습니다. 7. 회사는 회원이 서비스를 이용하면서 얻은 자료로 인한 손해에 대하여 책임을 지지 않습니다. 또한 회사는 회원이 서비스를 이용하며 타 회원으로 인해 입게 되는 정신적 피해에 대하여 보상할 책임을 지지 않습니다. 8. 회원이 서비스 화면에 게재한 정보, 자료, 사실 등의 내용에 관한 신뢰도 혹은 정확성에 대하여는 해당회원이 책임을 부담하며, 회사는 내용의 부정확 또는 허위로 인해 회원 또는 제3자에게 발생한 손해에 대하여는 아무런 책임을 부담하지 않습니다. 9. 회사는 회원 상호 간 및 회원과 제3자 상호 간에 서비스를 매개로 발생한 분쟁에 대해 개입할 의무가 없으며, 이로 인한 손해를 배상할 책임도 없습니다. 10. 회사는 서비스 이용과 관련하여 회원의 고의 또는 과실로 인하여 회원 또는 제3자에게 발생한 손해에 대하여는 아무런 책임을 부담하지 않습니다. 11. 회사에서 회원에게 무료로 제공하는 서비스의 이용과 관련해서는 개인정보보호정책에서 정하는 내용을 제외하고 어떠한 손해도 책임을 지지 않습니다.</li>
              <li>제 34 조 (분쟁의 해결)</li>
              <li>1. 회사는 회원이 제기하는 의견이나 불만에 대하여 적절하고 신속하게 처리하고, 그 결과를 통지합니다. 다만, 신속한 처리가 곤란한 경우에 회사는 회원에게 그 사유와 처리일정을 통보합니다. 2. 회사는 회원이 제기한 의견 등이 정당하지 않음을 이유로 처리하지 않은 경우 이의 사유를 통보합니다.</li>
              <li>제 35 조 (재판권 및 준거법)</li>
              <li>1. 회사와 회원 간의 서비스 이용에 관한 소송은 제소 당시의 회원의 주소에 의하고, 주소가 없는 경우 거소를 관할하는 지방법원의 전속관할로 합니다. 2. 제소 당시 회원의 주소 또는 거소가 분명하지 아니한 경우 관할법원은 「민사소송법」에 따라 정합니다.</li>
            </ul>
          </section>
          
          <section>
            <h2>[부칙] 본 약관은 2018년 12월 10일부터 적용됩니다.</h2>
          </section>
        </div>
      </div>

      <!-- 개인정보처리방침 -->
      <div v-if="modalContent === 'privacy'" class="policy-section">
        <h1 class="policy-title">개인정보처리방침</h1>
        <div class="policy-content">
          <section>
            <h2>LookGood(이하 '회사'라 합니다)는 사용자의 개인정보를 안전하게 보호하는 것을 가장 중요하게 생각합니다.</h2>
            <p>회사는 사용자가 신뢰할 수 있도록, 사용자가 제공한 개인정보를 목적에 따라 적법하게 사용하고, 제공한 개인정보에 대한 사용자의 권리와 선택을 보장하기 위해 최선을 다합니다. 이를 위해 제공된 개인정보가 어떤 목적으로 사용되고, 어떻게 보호되는지, 투명하게 공개합니다. 또한 사용자가 어떤 권리를 갖는지, 그 권리를 어떻게 행사할 수 있는지 자세히 알려드립니다.</p>
          </section>
          
          <section>
            <h2>주요 개인정보 처리 표시(라벨링)</h2>
            <ul>
              <li>개인정보 수집: 성명, 생년월일, 휴대전화번호, 이메일 주소 등</li>
              <li>처리 목적: 개인 식별, 서비스 관련 공지사항 전달 및 서비스 개선 관련 설문 전달, 문의사항 응대 등</li>
              <li>보유 기간: 회원 탈퇴 시까지, 다른 법령에 따라 일정 기간 개인정보를 보관해야 할 경우, 해당 기간 동안 보관 후 삭제 등</li>
              <li>개인정보 처리위탁, 제3자 제공, 고충처리부서</li>
              <li>처리위탁:</li>
              <ul>
                <li>㈜멀티캠퍼스, 나이스평가정보㈜, LookGood 시스템 운영팀 등</li>
              </ul>
              <li>제3자 제공:</li>
              <ul>
                <li>LookGood 파트너사 및 관련 협력 기관</li>
              </ul>
              <li>고충처리부서:</li>
              <ul>
                <li>LookGood 고객지원팀</li>
                <li>문의처: 이경석 님께 전화하세요.</li>
              </ul>
              <li>개인정보 수집 항목 및 방법</li>
              <ul>
                <li>필수항목</li>
                <ul>
                  <li>E-mail(ID), 성명, 비밀번호</li>
                  <li>생년월일, 휴대폰번호, 비상연락처, 주소, 학력사항 등</li>
                </ul>
                <li>선택항목</li>
                <ul>
                  <li>어학사항, 경력사항, 운동 관련 자격사항, 운동 경험 등</li>
                </ul>
                <li>고유식별정보</li>
                <ul>
                  <li>주민등록번호, 여권번호, 운전면허번호 (법적 요건에 따른 경우에 한함)</li>
                </ul>
                <li>개인정보 수집∙이용 목적</li>
                <ul>
                  <li>교육 운영: LookGood 운동 교육 수강생 본인 확인 강의 자료 제공, 수업 일정 관리, 커뮤니티 운영 등</li>
                  <li>서비스 개선: 고객 문의 응대, 설문 조사 및 서비스 품질 향상 기타: 이벤트 참여 관리 및 고객 편의성 개선 개인정보의 제3자 제공 회사는 이용자의 개인정보를 원칙적으로 외부에 제공하지 않습니다. 다만, 아래의 경우 예외로 합니다: 법적 의무를 준수하기 위한 경우 서비스 제공에 따른 요금 정산이 필요한 경우 기타 사용자의 동의를 받은 경우 개인정보 처리방침 업데이트 회사는 개인정보 처리방침의 변경사항이 있을 경우, 이를 반영하여 즉시 업데이트합니다. 변경된 내용은 홈페이지의 '공지사항'을 통해 사용자에게 안내드리며, 이전 정책은 상단에서 확인 가능합니다. 개인정보처리방침 버전번호 : 1.0.8 개인정보처리방침 시행일자 : 2024.08.02</li>
                </ul>
              </ul>
            </ul>
          </section>
        </div>
      </div>

      <!-- 이용안내 -->
      <div v-if="modalContent === 'guide'" class="policy-section">
        <h1 class="policy-title">이용안내</h1>
        <div class="policy-content">
          <section>
            <h2>1. 회원가입 및 로그인</h2>
            <p>본 서비스는 회원가입을 통해 이용 가능합니다. 회원가입 시, 이름, 이메일 주소, 비밀번호와 같은 필수 정보를 정확히 기재해주세요. 로그인 후, 다양한 개인 맞춤형 서비스와 콘텐츠를 이용할 수 있습니다.</p>
          </section>
          
          <section>
            <h2>2. 서비스 이용 시간</h2>
            <p>본 사이트는 연중무휴, 24시간 운영됩니다. 다만, 시스템 점검이나 기술적 문제가 발생할 경우 서비스 이용이 일시적으로 제한될 수 있습니다. 정기 점검 시간은 사전에 공지사항을 통해 안내드립니다.</p>
          </section>
          
          <section>
            <h2>3. 콘텐츠 이용</h2>
            <p>사이트 내 모든 콘텐츠는 저작권법의 보호를 받으며, 무단 복제 및 배포는 금지됩니다. 학습 자료, 동영상 강의, 게시물 등은 개인적인 목적으로만 사용 가능하며, 상업적 용도로 사용할 경우 사전 승인이 필요합니다.</p>
          </section>
          
          <section>
            <h2>4. 고객센터</h2>
            <p>이용 중 문제가 발생하거나 궁금한 사항이 있을 경우, 고객센터를 통해 문의하실 수 있습니다. 고객센터 운영시간: 평일 오전 9시 ~ 오후 6시 (점심시간: 12시 ~ 1시) 문의 방법: 이경석 님께 전화하세요. 1:1 문의 게시판: 고객센터 바로가기 5. 회원 정보 관리 회원은 "마이페이지"를 통해 자신의 개인정보를 수정하거나 회원 탈퇴를 요청할 수 있습니다. 비밀번호는 정기적으로 변경하여 계정을 안전하게 보호해주세요. 6. 금지 행위 타인의 계정을 도용하거나 불법적인 활동을 통해 서비스를 이용하는 경우, 관련 법령에 따라 처벌될 수 있습니다. 사이트 내 게시물 및 댓글에 욕설, 명예훼손, 스팸 등 부적절한 내용을 게시하는 행위는 금지됩니다. 7. 기타 안내 서비스 이용 중 발생하는 모든 데이터의 저장 및 처리는 회사의 개인정보 처리방침에 따라 이루어집니다. 서비스 이용약관과 개인정보 처리방침은 사이트 하단에서 확인하실 수 있습니다.</p>
          </section>
        </div>
      </div>
    </Modal>



    <!-- Center Buttons -->
    <div class="center-buttons">
      <div class="button">
        <div class="button-background"></div>
        <RouterLink to="/community/inquiry" style="text-decoration: none;">
          <span class="button-text">고객센터</span>
        </RouterLink>
      </div>
      <div class="button">
        <div class="button-background"></div>
        <RouterLink to="/community/inquiry" style="text-decoration: none;">
          <span class="button-text">질문게시판</span>
        </RouterLink>
      </div>
    </div>

    <!-- Text -->
    <div class="copyright">
      <p>본 사이트의 콘텐츠는 저작권법의 보호를 받는 바 무단 전재, 복사, 배포 등을 금합니다.</p>
      <p>Copyright(c) LookGood All rights reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import facebook0 from '@/assets/img/facebook0.svg';
import instagram0 from '@/assets/img/instagram0.svg';
import share20 from '@/assets/img/share-20.svg';
import youtube0 from '@/assets/img/youtube0.svg';
import Modal from './Modal.vue';

const isCopied = ref(false); // 복사 알림 상태

const copyLink = () => {
  const linkToCopy = window.location.href; // 현재 페이지 링크 가져오기

  if (navigator.clipboard && navigator.clipboard.writeText) {
    // 클립보드 API 지원 확인
    navigator.clipboard.writeText(linkToCopy)
      .then(() => {
        isCopied.value = true; // 알림 표시
        setTimeout(() => {
          isCopied.value = false; // 2초 후 알림 숨기기
        }, 2000);
      })
      .catch(err => {
        console.error('링크 복사 실패:', err);
      });
  } else {
    // 클립보드 API가 지원되지 않는 경우
    alert('클립보드 복사가 지원되지 않는 환경입니다.');
  }
};


const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
};

const modalContent = ref(''); // 모달 내용
const isModalOpen = ref(false); // 모달 열림 여부

const openModal = (content) => {
  modalContent.value = content;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};



</script>

<style scoped>
/* Footer 전체 컨테이너 수정 */
.footer-container {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: all 0.3s ease;
  border-top: 1px solid var(--border-color);
  padding: 20px 0;
  width: 95%; /* 헤더와 동일한 너비 */
  margin: 0 auto;
}

/* 로고는 색상 유지 */
.logo {
  color: #ebd03b !important;
}

/* Bottom Menu 스타일 수정 */
.bottom-menu {
  display: flex;
  justify-content: center;
  gap: 60px;
  font-size: 18px;
  margin: 20px auto;
  padding: 0;
  color: var(--text-color);
}

.bottom-menu div {
  color: var(--text-color);
  transition: color 0.3s ease;
  font-size: 16px;
  font-weight: 400;
  cursor: pointer;
}

/* highlight 클래스는 색상 유지 */
.highlight {
  color: #c2ad31 !important;
}

/* 버튼 스타일 수정 */
.button {
  position: relative;
  width: 180px;
  height: 55px;
  transition: all 0.3s ease;
}

.button-background {
  background: var(--button-bg, #ddc434);
  border-radius: 30px;
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  transition: background-color 0.3s ease;
}

/* 다크모드 버튼 스타일 */
:root.dark-mode .button-background {
  background: var(--dark-button-bg, #4a4a4a);
}

:root.dark-mode .button:hover .button-background {
  background: var(--dark-button-hover-bg, #666);
}

/* 버튼 텍스트 */
.button-text {
  position: relative;
  color: var(--button-text-color, #fff) !important;
  font-size: 18px;
  line-height: 55px;
  font-weight: 500;
  z-index: 1;
  text-align: center;
  width: 100%;
  display: block;
  transition: color 0.3s ease;
}

/* 버튼 호버 효과 */
.button:hover {
  transform: translateY(-2px);
}

.button:hover .button-background {
  filter: brightness(1.1);
}

/* 다크모드 CSS 변수 추가 */
:root.dark-mode {
  --button-bg: #4a4a4a;
  --button-hover-bg: #666;
  --button-text-color: #fff;
}
.copy-alert {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #000;
  color: #fff;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 14px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  animation: fadeInOut 2s ease;
}

/* 알림 메시지 애니메이션 */
@keyframes fadeInOut {
  0%, 90% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}


/* Copyright Text */
.copyright {
  text-align: center;
  color: var(--text-color);
  font-size: 13px;
  line-height: 1.6;
  max-width: 800px;
  margin: 30px auto 20px;
  padding: 0;
}

.copyright p {
  color: var(--text-color);
  margin: 5px 0;
}

/* Social Icons */
.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px auto;
  padding: 0;
}

.icon {
  width: 40px;
  height: 40px;
  filter: var(--icon-filter);
}

/* 다크모드일 때 아이콘 색상 조정 */
:root.dark-mode .icon {
  filter: brightness(0.8) invert(1);
}

/* 나머지 기존 스타일 유지 */
.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px auto 10px;
  padding: 0;
}
.logo {
  display: flex;
  color: #ebd03b;
  font-size: 38px;
  text-decoration: none;
  justify-content: center; /* Centers the logo content */
}
.logo-bold {
  font-weight: 600;
}

/* Apply rotation to each individual letter */
.logo-item:nth-child(1) { transform: rotate(-10deg); } /* L */
.logo-item:nth-child(2) { transform: rotate(10deg); }  /* oo */
.logo-item:nth-child(3) { transform: rotate(-10deg); } /* k */
.logo-item:nth-child(4) { transform: rotate(10deg); }  /* G */
.logo-item:nth-child(5) { transform: rotate(-10deg); } /* oo */
.logo-item:nth-child(6) { transform: rotate(10deg); }  /* d */

/* Social Media Icons */
.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px auto;
  padding: 0;
}
.icon {
  width: 40px;
  height: 40px;
}

/* Bottom Menu */
.bottom-menu {
  display: flex;
  justify-content: center;
  gap: 60px;
  font-size: 18px;
  margin: 20px auto;
  padding: 0;
}
.highlight {
  color: #c2ad31;
}

/* Center Buttons */
.center-buttons {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin: 30px auto;
  padding: 0;
}
.button {
  position: relative;
  width: 180px;
  height: 55px;
}
.button-background {
  background: #ddc434;
  border-radius: 30px;
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}
.button-text {
  position: relative;
  color: #fff;
  font-size: 18px;
  line-height: 55px;
  font-weight: 500;
  z-index: 1;
  text-align: center;
  width: 100%;
  display: block;
}

/* Copyright Text */
.copyright {
  color: #878787;
  font-size: 14px;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
}

.footer {
  background-color: var(--bg-color);
  color: var(--text-color);
  border-top: 1px solid var(--border-color);
  transition: background-color 0.3s, color 0.3s;
}

.footer-content {
  background-color: var(--bg-color);
  color: var(--text-color);
}

.footer-section {
  color: var(--text-color);
}

.footer-section h3 {
  color: var(--text-color);
}

.footer-section p,
.footer-section a {
  color: var(--text-color);
}

.footer-section a:hover {
  color: #ebd03b;
}

.footer-bottom {
  background-color: var(--secondary-bg);
  color: var(--text-color);
  border-top: 1px solid var(--border-color);
}

/* 기존 스타일은 유지하고, 다크모드 관련 스타일만 추가 */
.social-links a {
  color: var(--text-color);
}

.social-links a:hover {
  color: #ebd03b;
}

.contact-info a {
  color: var(--text-color);
}

.contact-info a:hover {
  color: #ebd03b;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .footer-container {
    width: 100%;
    padding: 20px;
  }

  .bottom-menu {
    gap: 30px;
    padding: 0 10px;
  }

  .social-icons {
    gap: 15px;
  }
}

/* 로고 관련 스타일 수정 및 추가 */
.logo-item {
  display: inline-block;
  transition: all 0.3s ease;
  position: relative;
  --rotation: 0deg; /* 기본 회전값 설정 */
}

/* 기본 회전 상태 */
.logo-item:nth-child(1) { --rotation: -10deg; }
.logo-item:nth-child(2) { --rotation: 10deg; }
.logo-item:nth-child(3) { --rotation: -10deg; }
.logo-item:nth-child(4) { --rotation: 10deg; }
.logo-item:nth-child(5) { --rotation: -10deg; }
.logo-item:nth-child(6) { --rotation: 10deg; }

/* 호버 시 애니메이션 효과 */
.logo:hover .logo-item {
  animation: wiggleJump 0.8s ease;
  animation-fill-mode: both;
}

/* 각 글자별 애니메이션 딜레이 */
.logo:hover .logo-item:nth-child(1) { animation-delay: 0s; }
.logo:hover .logo-item:nth-child(2) { animation-delay: 0.1s; }
.logo:hover .logo-item:nth-child(3) { animation-delay: 0.2s; }
.logo:hover .logo-item:nth-child(4) { animation-delay: 0.3s; }
.logo:hover .logo-item:nth-child(5) { animation-delay: 0.4s; }
.logo:hover .logo-item:nth-child(6) { animation-delay: 0.5s; }

/* 귀여운 통통 튀는 애니메이션 */
@keyframes wiggleJump {
  0% {
    transform: scale(1) rotate(var(--rotation));
  }
  10% {
    transform: scale(1.1) rotate(calc(var(--rotation) - 5deg));
  }
  20% {
    transform: scale(0.9) rotate(calc(var(--rotation) + 5deg));
  }
  30% {
    transform: translateY(-20px) scale(1.1) rotate(calc(var(--rotation) - 3deg));
  }
  50% {
    transform: translateY(0) scale(1.1) rotate(calc(var(--rotation) + 3deg));
  }
  70% {
    transform: translateY(-10px) scale(1.05) rotate(var(--rotation));
  }
  100% {
    transform: translateY(0) scale(1) rotate(var(--rotation));
  }
}

/* oo 글자 특별 효과 */
.logo-item.logo-bold {
  transition: all 0.3s ease;
}

.logo:hover .logo-bold {
  animation: squishyBounce 0.8s ease;
}

@keyframes squishyBounce {
  0%, 100% { transform: scale(1) rotate(var(--rotation)); }
  30% { transform: scaleX(1.2) scaleY(0.8) rotate(calc(var(--rotation) - 5deg)); }
  50% { transform: scaleX(0.8) scaleY(1.2) rotate(calc(var(--rotation) + 5deg)); }
  70% { transform: scale(1.1) rotate(var(--rotation)); }
}

/* 로고 컨테이너에 발광 효과 추가 */
.logo:hover {
  text-shadow: 0 0 15px rgba(235, 208, 59, 0.5),
               0 0 25px rgba(235, 208, 59, 0.3);
}

/* 로고 컨테이너 전체 효과 */
.logo-container {
  transition: all 0.3s ease;
  padding: 10px;
}

.logo-container:hover {
  transform: scale(1.02);
}
</style>