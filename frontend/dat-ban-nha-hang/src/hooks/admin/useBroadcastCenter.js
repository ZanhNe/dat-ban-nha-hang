import { useState } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

const useBroadcastCenter = () => {
    const [isSending, setIsSending] = useState(false);
    const [error, setError] = useState('');

    const handleSendNotification = async (formData) => {
        setIsSending(true);
        setError('');
        try {
            let res;


            if (formData.targetType === 'INDIVIDUAL') {

                if (!formData.targetUserId) {
                    alert("Vui lòng nhập ID người dùng!");
                    return false;
                }
                res = await adminService.sendPersonalNotification({
                    userId: parseInt(formData.targetUserId),
                    title: formData.title,
                    content: formData.content,
                    type: formData.type
                });
            } else {

                const payload = {
                    title: formData.title,
                    content: formData.content,
                    type: formData.type
                };
                if (formData.targetType === 'ROLE') {
                    payload.targetRole = formData.targetRole;
                }
                res = await adminService.sendBroadcastNotification(payload);
            }

            if (res.status === 200) {
                return { success: true, message: res.message };
            } else {
                return { success: false, message: res.message || "Gửi thất bại" };
            }
        } catch (error) {
            console.error("Lỗi khi gửi thông báo:", error);
            const apiError = formatApiError(error, 'Không thể gửi thông báo.');
            setError(apiError.displayMessage);
            return { success: false, message: apiError.displayMessage };
        } finally {
            setIsSending(false);
        }
    };

    return {
        isSending,
        error,
        handleSendNotification
    };
};

export default useBroadcastCenter;