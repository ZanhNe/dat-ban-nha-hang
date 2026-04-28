import React, { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import apiClient from '../../services/apiClient';
import './ReviewForm.css';

const USE_MOCK = true;

function ReviewForm() {
    const { restaurantId } = useParams();
    const [rating, setRating] = useState(0);
    const [content, setContent] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (rating === 0) return alert('Vui lòng chọn số sao');
        setSubmitting(true);
        try {
            if (USE_MOCK) {
                await new Promise(r => setTimeout(r, 800));
            } else {
                await apiClient.post(`/restaurants/${restaurantId}/reviews`, { rating, content });
            }
            setSubmitted(true);
        } catch (err) {
            console.error('Lỗi gửi đánh giá:', err);
        } finally {
            setSubmitting(false);
        }
    };

    if (submitted) {
        return (
            <div className="review-form-page">
                <div className="review-card">
                    <div className="success-msg">
                        ✅ Cảm ơn bạn đã đánh giá!
                    </div>
                    <Link to="/customer/bookings" className="btn-back-link">← Quay lại lịch sử đặt bàn</Link>
                </div>
            </div>
        );
    }

    return (
        <div className="review-form-page">
            <Link to="/customer/bookings" className="btn-back-link">← Quay lại</Link>
            <h2>Đánh giá nhà hàng</h2>
            <div className="review-card">
                <form onSubmit={handleSubmit}>
                    <div className="star-rating">
                        {[1, 2, 3, 4, 5].map(star => (
                            <button
                                key={star}
                                type="button"
                                className={`star ${star <= rating ? 'active' : ''}`}
                                onClick={() => setRating(star)}
                            >
                                {star <= rating ? '⭐' : '☆'}
                            </button>
                        ))}
                    </div>

                    <textarea
                        className="review-textarea"
                        placeholder="Chia sẻ trải nghiệm của bạn về nhà hàng..."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    />

                    <button type="submit" className="btn-submit-review" disabled={submitting || rating === 0}>
                        {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default ReviewForm;
