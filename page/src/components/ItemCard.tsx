import React from 'react';
import { RarityBadge } from './ui/RarityBadge';

interface ItemCardProps {
    item: {
        name: string;
        type: string;
        desc: string;
        rarity: string;
    };
}

export const ItemCard: React.FC<ItemCardProps> = ({ item }) => (
    <div className={`border-l-2 pl-4 bg-zinc-900/50 p-2 relative overflow-hidden border-l-${item.rarity === 'comun' ? 'gray' : item.rarity === 'raro' ? 'blue' : item.rarity === 'epico' ? 'purple' : item.rarity === 'legendario' ? 'yellow' : 'red'}-600`}>
        <div className="flex justify-between items-center mb-1 relative z-10">
            <strong className="text-lg text-gray-200">{item.name}</strong>
            <RarityBadge rarity={item.rarity} />
        </div>
        <p className="text-gray-500 text-sm relative z-10">{item.desc}</p>
        <span className="text-xs text-gray-600 uppercase mt-1 block relative z-10">{item.type}</span>
    </div>
);
