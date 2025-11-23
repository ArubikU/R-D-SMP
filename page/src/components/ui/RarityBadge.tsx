import React from 'react';

interface RarityBadgeProps {
    rarity: string;
}

export const RarityBadge: React.FC<RarityBadgeProps> = ({ rarity }) => {
    const map: Record<string, string> = {
        comun: "text-gray-400 border-gray-600 bg-gray-900/50",
        raro: "text-blue-400 border-blue-600 bg-blue-900/50",
        epico: "text-purple-400 border-purple-600 bg-purple-900/50",
        legendario: "text-yellow-400 border-yellow-600 bg-yellow-900/50",
        mitico: "text-red-500 border-red-600 bg-red-900/50 font-bold"
    };
    return (
        <span className={`px-2 py-0.5 text-xs uppercase tracking-wider rounded border ${map[rarity] || map.comun}`}>
            {rarity}
        </span>
    );
}
