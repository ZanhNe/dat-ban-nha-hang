import React from 'react';
import { Loader2 } from 'lucide-react';

const DetailMenu = ({ menus }) => {
    if (menus.length === 0) {
        return <div className="flex justify-center p-8"><Loader2 className="animate-spin text-orange-400" /></div>;
    }

    return (
        <div className="animate-fade-in">
            {menus.map((menuBlock, mbIdx) => (
                <div key={mbIdx}>
                    {menuBlock.restaurantMenu?.map((category, idx) => (
                        <div key={idx} className="mb-8">
                            <h2 className="text-xl font-extrabold text-gray-900 mb-4 pt-2">{category.groupName}</h2>
                            <div className="space-y-4">
                                {category.items?.map(item => (
                                    <div key={item.itemId} className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100 flex justify-between items-center hover:shadow-md transition">
                                        <div className="pr-4">
                                            <h3 className="font-bold text-gray-900 text-lg mb-1">{item.itemName}</h3>
                                            <p className="text-sm text-gray-500 line-clamp-2">{item.itemDescription}</p>
                                            <p className="text-orange-600 font-bold mt-2">{item.itemPrice?.toLocaleString()}đ</p>
                                        </div>
                                        <div className="w-20 h-20 bg-gray-200 rounded-xl overflow-hidden flex-shrink-0">
                                            <img src={item.itemImage || "https://images.unsplash.com/photo-1563379926898-05f45c51040c?auto=format&fit=crop&w=200&q=80"} alt="food" className="w-full h-full object-cover"/>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
};

export default DetailMenu;
