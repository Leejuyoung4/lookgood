import axios from 'axios';

class ChatbotService {
  constructor() {
    this.apiKey = import.meta.env.VITE_HUGGINGFACE_API_KEY;
    this.baseURL = 'https://api-inference.huggingface.co/models';
    this.model = 'facebook/blenderbot-400M-distill'; // 또는 다른 모델
  }

  async generateResponse(input, eventData) {
    try {
      // 먼저 미리 정의된 답변 확인
      const predefinedResponse = this.checkPredefinedResponses(input, eventData);
      if (predefinedResponse) return predefinedResponse;

      // AI 모델 사용
      const response = await axios.post(
        `${this.baseURL}/${this.model}`,
        { inputs: input },
        {
          headers: {
            'Authorization': `Bearer ${this.apiKey}`,
            'Content-Type': 'application/json',
          }
        }
      );

      return response.data.generated_text;
    } catch (error) {
      console.error('챗봇 응답 생성 오류:', error);
      return '죄송합니다. 일시적인 오류가 발생했습니다.';
    }
  }

  checkPredefinedResponses(input, eventData) {
    const patterns = {
      location: {
        keywords: ['어디', '위치', '주소', '가는 길'],
        response: () => `행사 장소는 ${eventData.address}입니다.`
      },
      time: {
        keywords: ['언제', '시간', '몇시'],
        response: () => `운영 시간은 ${eventData.checkInTime}부터 ${eventData.checkOutTime}까지입니다.`
      },
      parking: {
        keywords: ['주차', '차량'],
        response: () => `주차는 ${eventData.parkingAvailable ? '가능' : '불가능'}합니다.`
      },
      price: {
        keywords: ['가격', '비용', '얼마'],
        response: () => `입장료는 ${eventData.entryFee}원입니다.`
      }
    };

    for (const [key, pattern] of Object.entries(patterns)) {
      if (pattern.keywords.some(keyword => input.includes(keyword))) {
        return pattern.response();
      }
    }

    return null;
  }
}

export default new ChatbotService();